package com.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth.JwtTokenType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.UUID

fun Application.configureSecurity(jwtSettings: JwtSettings) {
    authentication {
        jwt {
            realm = jwtSettings.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSettings.secret))
                    .withAudience(jwtSettings.audience)
                    .withIssuer(jwtSettings.domain)
                    .build()
            )
            validate { credential ->
                val subject = credential.payload.subject
                val isValidAccessToken =
                    credential.payload.audience.contains(jwtSettings.audience) &&
                        credential.payload.getClaim("type").asString() == JwtTokenType.ACCESS &&
                        subject != null &&
                        runCatching { UUID.fromString(subject) }.isSuccess

                if (isValidAccessToken) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
