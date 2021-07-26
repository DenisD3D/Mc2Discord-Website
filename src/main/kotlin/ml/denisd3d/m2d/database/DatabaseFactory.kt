package ml.denisd3d.m2d.database

import com.kotlindiscord.kord.extensions.utils.env
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(UploadTable)
        }
    }

    /**
     * Look at hikari.properties and change accordingly
     * */
    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        if (env("DATABASE_URL") != null) {
            config.jdbcUrl = env("DATABASE_URL")
        } else {
            config.dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            config.username = env("DB_USERNAME") ?: error("Invalid DB settings")
            config.password = env("DB_PASSWORD") ?: error("Invalid DB settings")
            config.addDataSourceProperty("databaseName", env("DB_NAME") ?: error("Invalid DB settings"))
            config.addDataSourceProperty("portNumber", env("DB_PORT") ?: error("Invalid DB settings"))
            config.addDataSourceProperty("serverName", env("DB_SERVER") ?: error("Invalid DB settings"))
        }

        return HikariDataSource(config)
    }
}