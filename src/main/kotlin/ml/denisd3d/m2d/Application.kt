package ml.denisd3d.m2d

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.therandomlabs.curseapi.minecraft.CurseAPIMinecraft
import dev.kord.common.entity.Snowflake
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ml.denisd3d.m2d.database.DatabaseFactory
import ml.denisd3d.m2d.discord.extensions.LinkExtension
import ml.denisd3d.m2d.discord.extensions.ServerExtension
import ml.denisd3d.m2d.plugins.configureRouting
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

//DISCORD
private val TOKEN = env("TOKEN") ?: error("Env var TOKEN not provided")
val SERVER_ID = Snowflake(env("SERVER_ID")?.toLong() ?: error("Env var SERVER_ID not provided"))

@OptIn(ExperimentalTime::class)
suspend fun main() {
    CurseAPIMinecraft.initialize()

    embeddedServer(Netty, Integer.valueOf(env("PORT") ?: "80")) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
            outputFormat = HTMLOutputFormat.INSTANCE
        }
        install(IgnoreTrailingSlash)
        install(StatusPages) {
            exception<MissingRequestParameterException> { _ ->
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        DatabaseFactory.init()
        configureRouting()
    }.start(wait = false)

    val bot = ExtensibleBot(TOKEN) {
        slashCommands {
            enabled = true
        }
        extensions {
            help {
                deletePaginatorOnTimeout = true
                paginatorTimeout = Duration.minutes(5).inWholeMilliseconds
            }
            add(::LinkExtension)
            add(::ServerExtension)
        }
    }

    bot.start()
}

