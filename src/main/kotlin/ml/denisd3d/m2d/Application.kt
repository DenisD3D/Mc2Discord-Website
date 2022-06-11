package ml.denisd3d.m2d

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ml.denisd3d.m2d.database.DatabaseFactory
import ml.denisd3d.m2d.discord.extensions.LinkExtension
import ml.denisd3d.m2d.discord.extensions.ServerExtension
import ml.denisd3d.m2d.plugins.configureRouting
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

//DISCORD
private val TOKEN = env("TOKEN") ?: error("Env var TOKEN not provided")
val SERVER_ID = Snowflake(env("SERVER_ID")?.toLong() ?: error("Env var SERVER_ID not provided"))

val client = HttpClient(CIO) {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.NONE
    }
    install(JsonFeature) {
        serializer = GsonSerializer()
    }
}

@OptIn(ExperimentalTime::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
suspend fun main() {
    val fixedRateTimer = fixedRateTimer(name = "awake", daemon = true, initialDelay = TimeUnit.MINUTES.toMillis(1), period = TimeUnit.MINUTES.toMillis(20)) {
        GlobalScope.launch (Dispatchers.IO) {
            println(client.get<HttpStatement>(env("URL") + "/ping") {
                method = HttpMethod.Get
            }.execute().content.readUTF8Line())
        }
    }


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

    fixedRateTimer.cancel()
}

