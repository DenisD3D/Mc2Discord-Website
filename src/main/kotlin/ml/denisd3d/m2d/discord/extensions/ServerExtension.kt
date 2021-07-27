package ml.denisd3d.m2d.discord.extensions

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.annotation.KordPreview
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.client.statement.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import ml.denisd3d.m2d.*

class ServerExtension : Extension() {
    override val name = "server"


    @OptIn(KordPreview::class)
    override suspend fun setup() {
        event<ReadyEvent> {
            action {
                getAccessToken()

                println("Discord logged in started")
            }
        }
        event<MessageCreateEvent> {
            action {
                println(event.message)
            }
        }

        slashCommand {
            name = "start"
            description = "Start Mc2Discord demo server"
            guild = SERVER_ID
            autoAck = AutoAckType.PUBLIC

            action {
                startServer()
                publicFollowUp {
                    content = "Server is starting..."
                }
            }
        }

        slashCommand {
            name = "stop"
            description = "Stop Mc2Discord demo server"
            guild = SERVER_ID
            autoAck = AutoAckType.PUBLIC

            action {
                stopServer()
                publicFollowUp {
                    content = "Server is stopping"
                }
            }
        }

    }
}