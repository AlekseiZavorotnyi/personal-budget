package com.routes

import com.auth.AuthException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import java.util.UUID

internal fun ApplicationCall.requiredUserId(): UUID {
    val subject = principal<JWTPrincipal>()?.payload?.subject
        ?: throw AuthException(HttpStatusCode.Unauthorized, "Access token is required")

    return runCatching { UUID.fromString(subject) }
        .getOrElse { throw AuthException(HttpStatusCode.Unauthorized, "Invalid access token") }
}
