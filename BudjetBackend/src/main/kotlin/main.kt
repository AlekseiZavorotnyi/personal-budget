package com

import com.database.DatabaseFactory

fun main(args: Array<String>) {
    if ((System.getenv("APP_MODE") ?: "").equals("migrate", ignoreCase = true)) {
        val settings = loadAppSettingsFromEnvironment()
        DatabaseFactory.migrate(settings.database)
        return
    }

    io.ktor.server.netty.EngineMain.main(args)
}
