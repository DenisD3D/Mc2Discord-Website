package ml.denisd3d.m2d.discord.extensions

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.extensions.Extension
import dev.kord.common.annotation.KordPreview
import dev.kord.core.event.gateway.ReadyEvent
import ml.denisd3d.m2d.SERVER_ID
import ml.denisd3d.m2d.getAccessToken
import ml.denisd3d.m2d.startServer
import ml.denisd3d.m2d.stopServer

class ServerExtension : Extension() {
    override val name = "server"


    @OptIn(KordPreview::class)
    override suspend fun setup() {
        event<ReadyEvent> {
            action {
                getAccessToken()
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