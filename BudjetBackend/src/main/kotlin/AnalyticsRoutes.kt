package com

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAnalyticsRoutes() {
    route("/analytics") {
        get("/summary") {
            call.respond(
                MockBudgetApi.analyticsSummary(
                    from = call.request.queryParameters["from"],
                    to = call.request.queryParameters["to"]
                )
            )
        }

        get("/by-category") {
            call.respond(
                MockBudgetApi.analyticsByCategory(
                    from = call.request.queryParameters["from"],
                    to = call.request.queryParameters["to"]
                )
            )
        }

        get("/by-period") {
            call.respond(
                MockBudgetApi.analyticsByPeriod(
                    grouping = call.request.queryParameters["period"] ?: "day",
                    from = call.request.queryParameters["from"],
                    to = call.request.queryParameters["to"]
                )
            )
        }
    }
}
