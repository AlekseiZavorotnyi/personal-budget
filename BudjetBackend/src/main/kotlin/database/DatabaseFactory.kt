package com.database

import com.config.DatabaseSettings
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun connect(settings: DatabaseSettings) {
        Database.connect(
            url = settings.url,
            driver = settings.driver,
            user = settings.user,
            password = settings.password
        )
    }

    fun migrate(settings: DatabaseSettings) {
        Flyway.configure()
            .locations("classpath:db/migration")
            .validateMigrationNaming(true)
            .dataSource(settings.url, settings.user, settings.password)
            .driver(settings.driver)
            .load()
            .migrate()
    }

    fun ping(): Boolean = transaction {
        BudgetEntriesTable.selectAll().limit(1).count()
        true
    }
}
