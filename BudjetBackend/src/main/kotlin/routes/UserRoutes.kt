package com.routes

import com.database.ApiMessage
import com.database.UserProfileUpdateRequest
import com.database.UsersTable
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerUserRoutes() {
    authenticate {
        route("/users") {
            patch("/me") {
                val request = call.receiveNullable<UserProfileUpdateRequest>()
                    ?: throw IllegalArgumentException("Request body is required")
                call.respond(UsersTable.updateProfile(call.requiredUserId(), request))
            }

            delete("/me") {
                val deleted = UsersTable.delete(call.requiredUserId())

                if (deleted) {
                    call.respond(ApiMessage("Account deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiMessage("User not found"))
                }
            }
        }
    }
}
