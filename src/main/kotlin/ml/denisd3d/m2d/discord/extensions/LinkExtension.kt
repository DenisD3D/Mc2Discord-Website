package ml.denisd3d.m2d.discord.extensions

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.env
import com.therandomlabs.curseapi.CurseAPI
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.builder.interaction.embed
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import ml.denisd3d.m2d.SERVER_ID
import java.net.URL

class LinkExtension : Extension() {
    override val name = "link"

    @OptIn(KordPreview::class)
    override suspend fun setup() {
        slashCommand {
            name = "mc2discord"
            description = "Return mc2discord links"

            guild = SERVER_ID

            autoAck = AutoAckType.PUBLIC

            action {
                publicFollowUp {
                    embed {
                        author {
                            name = "Mc2Discord"
                            icon =
                                "https://cdn.discordapp.com/attachments/715227915297751100/865999325078880266/636972532344008381.png"
                        }
                        description = "Link your Minecraft chat with your Discord"

                        field {
                            name = "Curseforge (Download):"
                            value = "https://www.curseforge.com/minecraft/mc-mods/mc2discord"
                        }
                        field {
                            name = "Github (Source code) :"
                            value = "https://github.com/DenisD3D/Mc2Discord"
                        }
                        field {
                            name = "Wiki (How to install) :"
                            value = "https://github.com/DenisD3D/Mc2Discord/wiki"
                        }
                    }
                    components {
                        val files = CurseAPI.project(325235).get().files();
                        env("FORGE_VERSIONS")?.split(",")?.forEach { version ->
                            val filesCopy = files.clone()
                            val versions = version.split("/")
                            filesCopy.filter { curseFile -> curseFile.gameVersionStrings().contains(versions[0]) }
                            val curseVersion = filesCopy.maxByOrNull { curseFile -> curseFile.uploadTime() }
                            linkButton {
                                label = versions[1]
                                partialEmoji = DiscordPartialEmoji(Snowflake(866734907001536542), "download")
                                url = curseVersion?.url().toString()
                            }
                        }
                    }
                }
            }
        }

    }
}