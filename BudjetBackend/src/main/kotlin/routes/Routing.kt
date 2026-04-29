package com.routes

import com.config.JwtSettings
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(jwtSettings: JwtSettings) {
    routing {
        registerSystemRoutes()
        registerPwaRoutes()

        route("/api") {
            registerApiSystemRoutes()
            registerAuthRoutes(jwtSettings)
            registerUserRoutes()
            registerTransactionRoutes()
            registerCategoryRoutes()
        }
    }
}
