package com

import io.ktor.server.application.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

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
                if (credential.payload.audience.contains(jwtSettings.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
