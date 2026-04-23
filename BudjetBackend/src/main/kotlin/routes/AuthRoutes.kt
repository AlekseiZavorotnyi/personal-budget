package com.routes

import com.mock.LoginRequest
import com.mock.MockBudgetApi
import com.mock.RegisterRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAuthRoutes() {
    route("/auth") {
        post("/register") {
            val request = call.receiveNullable<RegisterRequest>()
            call.respond(HttpStatusCode.Created, MockBudgetApi.register(request))
        }

        post("/login") {
            val request = call.receiveNullable<LoginRequest>()
            call.respond(MockBudgetApi.login(request))
        }

        post("/logout") {
            call.respond(MockBudgetApi.logout())
        }

        post("/refresh") {
            call.respond(MockBudgetApi.refresh())
        }

        get("/me") {
            call.respond(MockBudgetApi.currentUser())
        }
    }
}
