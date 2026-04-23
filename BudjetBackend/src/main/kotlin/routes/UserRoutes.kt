package com.routes

import com.mock.MockBudgetApi
import com.mock.UpdateProfileRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerUserRoutes() {
    route("/users") {
        patch("/me") {
            val request = call.receiveNullable<UpdateProfileRequest>()
            call.respond(MockBudgetApi.updateProfile(request))
        }

        delete("/me") {
            call.respond(MockBudgetApi.deleteProfile())
        }
    }
}
