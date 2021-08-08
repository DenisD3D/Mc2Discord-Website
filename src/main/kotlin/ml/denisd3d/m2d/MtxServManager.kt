package ml.denisd3d.m2d

import com.kotlindiscord.kord.extensions.utils.env
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val MTXSERV_CLIENT_ID = env("MTXSERV_CLIENT_ID")
val MTXSERV_CLIENT_SECRET = env("MTXSERV_CLIENT_SECRET") ?: error("Env var MTXSERV_CLIENT_SECRET not provided")
val MTXSERV_API_KEY = env("MTXSERV_API_KEY") ?: error("Env var MTXSERV_API_KEY not provided")
val MTXSERV_GAME_SERVER_ID = env("MTXSERV_GAME_SERVER_ID") ?: error("Env var MTXSERV_GAME_SERVER_ID not provided")

val oauthTokenUrl = "https://mtxserv.com/oauth/v2/token"
val startServerUrl = "https://mtxserv.com/api/v1/game/${MTXSERV_GAME_SERVER_ID}/actions/start"
val stopServerUrl = "https://mtxserv.com/api/v1/game/${MTXSERV_GAME_SERVER_ID}/actions/stop"

var tokenResponse: TokenResponse? = null

var current_time: Long = 0

data class TokenResponse(val access_token: String, val expires_in: Long, val token_type: String, val scope: String, val refresh_token: String, )

suspend fun getAccessToken() {
    current_time = System.currentTimeMillis()
    tokenResponse = client.get<HttpStatement>(oauthTokenUrl) {
        method = HttpMethod.Get
        parameter("grant_type", "https://mtxserv.com/grants/api_key")
        parameter("client_id", MTXSERV_CLIENT_ID)
        parameter("client_secret", MTXSERV_CLIENT_SECRET)
        parameter("api_key", MTXSERV_API_KEY)
    }.receive()
}

suspend fun startServer() {
    if (current_time + 3400 * 1000 < System.currentTimeMillis())
        getAccessToken()
    client.get<HttpStatement>(startServerUrl) {
        method = HttpMethod.Post
        parameter("access_token", tokenResponse?.access_token)
    }.execute()
}

suspend fun stopServer() {
    if (current_time + 3400 * 1000 < System.currentTimeMillis())
        getAccessToken()
    client.get<HttpStatement>(stopServerUrl) {
        method = HttpMethod.Post
        parameter("access_token", tokenResponse?.access_token)
    }.execute()
}