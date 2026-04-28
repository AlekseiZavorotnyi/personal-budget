package com

import com.config.configureHttp
import com.config.configureSecurity
import com.config.configureSerialization
import com.config.configureStatusPages
import com.config.loadAppSettings
import com.database.DatabaseFactory
import com.routes.configureRouting
import io.ktor.server.application.*

fun Application.module(testing: Boolean = false) {
    val settings = loadAppSettings()

    configureHttp()
    configureSerialization()
    configureSecurity(settings.jwt)
    configureStatusPages()
    configureRouting(settings.jwt)

    if (!testing) {
        DatabaseFactory.connect(settings.database)
        DatabaseFactory.migrate(settings.database)
    }
}
