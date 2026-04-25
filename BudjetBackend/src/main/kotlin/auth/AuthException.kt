package com.auth

import io.ktor.http.HttpStatusCode

class AuthException(
    val status: HttpStatusCode,
    override val message: String
) : RuntimeException(message)
