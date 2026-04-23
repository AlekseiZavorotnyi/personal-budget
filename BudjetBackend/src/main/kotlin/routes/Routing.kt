package com.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        registerSystemRoutes()
        registerPwaRoutes()

        route("/api") {
            registerApiSystemRoutes()
            registerAuthRoutes()
            registerUserRoutes()
            registerTransactionRoutes()
            registerCategoryRoutes()
            registerAnalyticsRoutes()
            registerReportRoutes()
            registerBudgetRoutes()
            registerSyncRoutes()
        }
    }
}
