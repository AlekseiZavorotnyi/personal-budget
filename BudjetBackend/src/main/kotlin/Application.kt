package com

import com.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.module(testing: Boolean = false) {
    val settings = loadAppSettings()

    if (!testing) {
        DatabaseFactory.connect(settings.database)
        DatabaseFactory.migrate(settings.database)
    }

    configureHttp()
    configureSerialization()
    configureSecurity(settings.jwt)
    configureStatusPages()
    configureRouting()
}
