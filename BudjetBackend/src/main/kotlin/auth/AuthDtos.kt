package com.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)

@Serializable
data class LoginRequest(
    val email: String? = null,
    val password: String? = null
)

@Serializable
data class RefreshRequest(
    val refreshToken: String? = null
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val currency: String,
    val timezone: String,
    val mocked: Boolean = false
)

@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val mocked: Boolean = false
)

@Serializable
data class AuthSessionResponse(
    val user: UserProfileResponse,
    val tokens: TokenPair,
    val mocked: Boolean = false
)

@Serializable
data class AuthMessageResponse(
    val message: String,
    val mocked: Boolean = false
)

@Serializable
data class AuthErrorResponse(
    val message: String,
    val mocked: Boolean = false
)
