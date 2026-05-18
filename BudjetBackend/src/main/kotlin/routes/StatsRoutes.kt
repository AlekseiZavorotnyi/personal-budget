package com.routes

import com.database.StatsQueries
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.time.LocalDate

fun Route.registerStatsRoutes() {
    authenticate {
        route("/stats") {
            get("/summary") {
                val from = call.request.queryParameters["from"]
                val to = call.request.queryParameters["to"]
                call.respond(StatsQueries.summary(call.requiredUserId(), from, to))
            }

            get("/by-category") {
                val from = call.request.queryParameters["from"]
                val to = call.request.queryParameters["to"]
                val type = call.request.queryParameters["type"]
                call.respond(StatsQueries.byCategory(call.requiredUserId(), from, to, type))
            }

            get("/monthly") {
                val year = call.request.queryParameters["year"]?.toIntOrNull()
                    ?: LocalDate.now().year
                call.respond(StatsQueries.monthly(call.requiredUserId(), year))
            }
        }
    }
}
