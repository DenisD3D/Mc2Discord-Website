import os
import time
import requests


class MTXServClient:
    def __init__(self, client_id: str, client_secret: str, api_key: str, game_server_id: str):
        self.client_id = client_id
        self.client_secret = client_secret
        self.api_key = api_key
        self.game_server_id = game_server_id

        self.oauth_token_url = "https://mtxserv.com/oauth/v2/token"
        self.start_server_url = f"https://mtxserv.com/api/v1/game/{self.game_server_id}/actions/start"
        self.stop_server_url = f"https://mtxserv.com/api/v1/game/{self.game_server_id}/actions/stop"

        self.token_response = None
        self.current_time = 0

    def get_access_token(self):
        self.current_time = int(time.time() * 1000)
        response = requests.get(self.oauth_token_url, params={
            "grant_type": "https://mtxserv.com/grants/api_key",
            "client_id": self.client_id,
            "client_secret": self.client_secret,
            "api_key": self.api_key
        })
        response.raise_for_status()
        self.token_response = response.json()

    def start_server(self):
        self.send_request(self.start_server_url)

    def stop_server(self):
        self.send_request(self.stop_server_url)

    def send_request(self, url: str):
        if self.current_time + 3400 * 1000 < int(time.time() * 1000):
            self.get_access_token()

        headers = {
            "Authorization": f"Bearer {self.token_response.get('access_token')}"
        }
        response = requests.post(url, headers=headers)
        response.raise_for_status()