package com.routes

import com.mock.MockBudgetApi
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerReportRoutes() {
    route("/reports") {
        get("/monthly") {
            val year = call.request.queryParameters["year"]?.toIntOrNull() ?: 2026
            val month = call.request.queryParameters["month"]?.toIntOrNull() ?: 4
            call.respond(MockBudgetApi.monthlyReport(year, month))
        }

        get("/yearly") {
            val year = call.request.queryParameters["year"]?.toIntOrNull() ?: 2026
            call.respond(MockBudgetApi.yearlyReport(year))
        }
    }
}
