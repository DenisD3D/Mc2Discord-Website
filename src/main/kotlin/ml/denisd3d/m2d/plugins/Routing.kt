package ml.denisd3d.m2d.plugins

import com.kotlindiscord.kord.extensions.utils.env
import com.therandomlabs.curseapi.CurseAPI
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import ml.denisd3d.m2d.model.Upload
import ml.denisd3d.m2d.repo.UploadRepo
import java.util.*

val uploadRepo = UploadRepo()

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    routing {
        val files = CurseAPI.project(325235).get().files();
        get("/") {
            val versionMap = mutableMapOf<String, String>()
            env("FORGE_VERSIONS")?.split(",")?.forEach { version ->
                val filesCopy = files.clone()
                val versions = version.split("/")
                filesCopy.filter { curseFile -> curseFile.gameVersionStrings().contains(versions[0]) }
                val curseVersion = filesCopy.maxByOrNull { curseFile -> curseFile.uploadTime() }
                versionMap[versions[1]] = curseVersion?.url().toString()
            }
            call.respond(FreeMarkerContent("index.ftl", mapOf("versions" to versionMap), ""))
        }
        post("/api/v1/upload/") {
            val formParameters = call.receiveParameters()

            val uuid = UUID.randomUUID().toString()
            uploadRepo.create(
                Upload(
                    uuid,
                    formParameters.getOrFail("config"),
                    formParameters.getOrFail("errors"),
                    formParameters.getOrFail("env"),
                )
            )
            call.respondText(env("URL") + "/uploads/view?key=$uuid")
        }
        get("/uploads/view") {
            val upload = uploadRepo.getOrFail(call.parameters.getOrFail("key"))
            call.respond(FreeMarkerContent("uploads/view.ftl", mapOf("upload" to upload), ""))
        }
        get("/ping"){
            call.respond("pong")
        }
        static("/static") {
            resources("static")
        }
    }
}

open class ParameterException(message: String = "A parameter is missing") : Exception(message)