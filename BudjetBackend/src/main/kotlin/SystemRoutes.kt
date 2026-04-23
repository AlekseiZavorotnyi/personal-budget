package com

import com.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RootResponse(
    val service: String,
    val status: String
)

@Serializable
data class HealthResponse(
    val status: String,
    val database: String
)

fun Route.registerSystemRoutes() {
    get("/") {
        call.respond(
            RootResponse(
                service = "BudgetServer",
                status = "running"
            )
        )
    }

    get("/health") {
        call.respondHealth()
    }

    get("/json/kotlinx-serialization") {
        call.respond(mapOf("hello" to "budget"))
    }
}

fun Route.registerApiSystemRoutes() {
    get("/health") {
        call.respondHealth()
    }
}

private suspend fun ApplicationCall.respondHealth() {
    val databaseStatus = if (runCatching { DatabaseFactory.ping() }.getOrDefault(false)) {
        "up"
    } else {
        "down"
    }

    respond(
        HealthResponse(
            status = "ok",
            database = databaseStatus
        )
    )
}
