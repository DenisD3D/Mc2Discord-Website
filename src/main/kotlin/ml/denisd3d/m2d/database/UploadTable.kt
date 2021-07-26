package ml.denisd3d.m2d.database

import org.jetbrains.exposed.dao.id.UUIDTable

object UploadTable : UUIDTable() {
    val config = text("config")
    val errors = text("errors")
    val env = text("env")
}