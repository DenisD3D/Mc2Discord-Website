import logging
import os
import uuid

import psycopg
import requests
from dotenv import load_dotenv
from flask import Flask, request, render_template, abort, make_response
from flask_discord_interactions import DiscordInteractions, Context

from mtxserv import MTXServClient

load_dotenv()

# Flask setup
app = Flask(__name__, template_folder='templates')
app.url_map.strict_slashes = False
discord = DiscordInteractions(app)
app.config["DISCORD_CLIENT_ID"] = os.getenv("DISCORD_CLIENT_ID")
app.config["DISCORD_PUBLIC_KEY"] = os.getenv("DISCORD_PUBLIC_KEY")
app.config["DISCORD_CLIENT_SECRET"] = os.getenv("DISCORD_CLIENT_SECRET")

# PostgreSQL database configuration
DATABASE_CONFIG = {
    'dbname': os.getenv('DB_NAME'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD'),
    'host': os.getenv('DB_HOST', 'localhost'),
    'port': os.getenv('DB_PORT', 5432)
}

# MtxServ client configuration
mtxserv = MTXServClient(
    os.getenv("MTXSERV_CLIENT_ID"),
    os.getenv("MTXSERV_CLIENT_SECRET"),
    os.getenv("MTXSERV_API_KEY"),
    os.getenv("MTXSERV_GAME_SERVER_ID")
)


# Discord commands
@discord.command()
def start(_: Context):
    """Start Mc2Discord demo game server"""
    try:
        mtxserv.start_server()
        return "Server is starting... Please wait for confirmation message"
    except requests.RequestException as e:
        logging.error(f"MTXServ error: {e}")
        return "Failed to start the server"


@discord.command()
def stop(_: Context):
    """Stop Mc2Discord demo game server"""
    try:
        mtxserv.stop_server()
        return "Server is stopping"
    except requests.RequestException as e:
        logging.error(f"MTXServ error: {e}")
        return "Failed to stop the server"

# Flask routes
@app.get("/")
def index():
    return render_template("index.html")


@app.post("/api/v1/upload")
def upload():
    data = request.form
    if "config" not in data or "errors" not in data or "env" not in data:
        response = make_response("Missing required fields", 400)
        response.mimetype = "text/plain"
        return response

    key = str(uuid.uuid4())
    try:
        with psycopg.connect(**DATABASE_CONFIG) as conn:
            with conn.cursor() as cur:
                cur.execute("INSERT INTO uploads (id, config, errors, env) VALUES (%s, %s, %s, %s)", (key, data["config"], data["errors"], data["env"]))
    except psycopg.DatabaseError as e:
        logging.error(f"Database error: {e}")
        abort(500, "Database error")

    # Send a message to the Discord channel
    try:
        response = requests.post(os.getenv("DISCORD_WEBHOOK_URL"), json={
            "content": "New upload created: " + os.getenv("BASE_URL") + "/uploads/view?key=" + key
        })
        response.raise_for_status()
    except requests.RequestException as e:
        logging.error(f"Discord webhook error: {e}")

    return os.getenv("BASE_URL") + "/uploads/view?key=" + key


@app.get("/uploads/view")
def view():
    if "key" not in request.args:
        abort(400, "Missing required key parameter")

    key = request.args.get("key")

    try:
        uuid.UUID(key)
    except ValueError:
        abort(404, "Upload not found")

    try:
        with psycopg.connect(**DATABASE_CONFIG) as conn:
            with conn.cursor() as cur:
                cur.execute("SELECT * FROM uploads WHERE id = %s", (key,))
                result = cur.fetchone()

                if result is None:
                    abort(404, "Upload not found")
    except psycopg.DatabaseError as e:
        logging.error(f"Database error: {e}")
        abort(500, "Database error")

    return render_template("view.html", id=result[0], config=result[1], errors=result[2], env=result[3])


discord.set_route("/interactions")
discord.update_commands(guild_id=os.environ["DISCORD_GUILD_ID"])

# Entry point
if __name__ == '__main__':
    app.run(port=os.getenv('FLASK_PORT', 5000))
