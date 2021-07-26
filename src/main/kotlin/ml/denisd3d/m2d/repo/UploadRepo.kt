package ml.denisd3d.m2d.repo

import io.ktor.features.*
import io.ktor.http.*
import ml.denisd3d.m2d.database.UploadTable
import ml.denisd3d.m2d.model.Upload
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UploadRepo {

    suspend fun create(upload: Upload) {
        transaction {
            UploadTable.insert {
                it[id] = UUID.fromString(upload.id)
                it[config] = upload.config
                it[errors] = upload.errors
                it[env] = upload.env
            }
        }
    }

    suspend fun get(id: String): Upload? {
        return transaction {
            UploadTable.select { UploadTable.id eq UUID.fromString(id) }.map {
                it.toUpload()
            }.firstOrNull()
        }
    }
    suspend fun getOrFail(id: String): Upload {
        return get(id) ?: throw NotFoundException("Upload not found")
    }

    suspend fun getAll(): List<Upload> {
        return transaction {
            UploadTable.selectAll().map { it.toUpload() }
        }
    }

    private fun ResultRow.toUpload(): Upload {
        return Upload(this[UploadTable.id].toString(), this[UploadTable.config], this[UploadTable.errors], this[UploadTable.env])
    }
}