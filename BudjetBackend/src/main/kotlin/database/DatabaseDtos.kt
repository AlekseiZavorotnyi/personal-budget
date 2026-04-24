package com.database

import kotlinx.serialization.Serializable

@Serializable
data class UserRecord(
    val id: String,
    val email: String
)

@Serializable
data class CategoryRecord(
    val id: String,
    val userId: String,
    val name: String,
    val type: String
)

@Serializable
data class ApiMessage(
    val message: String,
    val mocked: Boolean = false
)
