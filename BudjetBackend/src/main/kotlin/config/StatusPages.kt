package com.config

import com.auth.AuthErrorResponse
import com.auth.AuthException
import com.database.ApiMessage
import com.database.TransactionNotFoundException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AuthException> { call, cause ->
            call.respond(cause.status, AuthErrorResponse(cause.message))
        }

        exception<TransactionNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiMessage(cause.message ?: "Transaction not found"))
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiMessage(cause.message ?: "Bad request"))
        }

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
}
