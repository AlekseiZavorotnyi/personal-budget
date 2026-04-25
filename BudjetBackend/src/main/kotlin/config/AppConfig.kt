package com.config

import io.ktor.server.application.*

private const val defaultDbUrl = "jdbc:postgresql://localhost:5432/budget"
private const val defaultDbUser = "budget"
private const val defaultDbPassword = "budget"
private const val defaultDbDriver = "org.postgresql.Driver"
private const val defaultJwtDomain = "http://localhost:8080"
private const val defaultJwtAudience = "budget-users"
private const val defaultJwtRealm = "Budget Server"
private const val defaultJwtSecret = "local-development-secret"

data class DatabaseSettings(
    val url: String,
    val user: String,
    val password: String,
    val driver: String
)

data class JwtSettings(
    val audience: String,
    val domain: String,
    val realm: String,
    val secret: String,
    val accessTokenTtlSeconds: Long = 3600,
    val refreshTokenTtlSeconds: Long = 2_592_000
)

data class AppSettings(
    val database: DatabaseSettings,
    val jwt: JwtSettings
)

fun Application.loadAppSettings(): AppSettings {
    return AppSettings(
        database = DatabaseSettings(
            url = resolveSetting(
                envName = "DB_URL",
                configValue = environment.config.propertyOrNull("database.url")?.getString(),
                defaultValue = defaultDbUrl
            ),
            user = resolveSetting(
                envName = "DB_USER",
                configValue = environment.config.propertyOrNull("database.user")?.getString(),
                defaultValue = defaultDbUser
            ),
            password = resolveSetting(
                envName = "DB_PASSWORD",
                configValue = environment.config.propertyOrNull("database.password")?.getString(),
                defaultValue = defaultDbPassword
            ),
            driver = resolveSetting(
                envName = "DB_DRIVER",
                configValue = environment.config.propertyOrNull("database.driver")?.getString(),
                defaultValue = defaultDbDriver
            )
        ),
        jwt = JwtSettings(
            domain = resolveSetting(
                envName = "JWT_DOMAIN",
                configValue = environment.config.propertyOrNull("jwt.domain")?.getString(),
                defaultValue = defaultJwtDomain
            ),
            audience = resolveSetting(
                envName = "JWT_AUDIENCE",
                configValue = environment.config.propertyOrNull("jwt.audience")?.getString(),
                defaultValue = defaultJwtAudience
            ),
            realm = resolveSetting(
                envName = "JWT_REALM",
                configValue = environment.config.propertyOrNull("jwt.realm")?.getString(),
                defaultValue = defaultJwtRealm
            ),
            secret = resolveSetting(
                envName = "JWT_SECRET",
                configValue = environment.config.propertyOrNull("jwt.secret")?.getString(),
                defaultValue = defaultJwtSecret
            )
        )
    )
}

fun loadAppSettingsFromEnvironment(): AppSettings {
    return AppSettings(
        database = DatabaseSettings(
            url = resolveSetting(
                envName = "DB_URL",
                defaultValue = defaultDbUrl
            ),
            user = resolveSetting(
                envName = "DB_USER",
                defaultValue = defaultDbUser
            ),
            password = resolveSetting(
                envName = "DB_PASSWORD",
                defaultValue = defaultDbPassword
            ),
            driver = resolveSetting(
                envName = "DB_DRIVER",
                defaultValue = defaultDbDriver
            )
        ),
        jwt = JwtSettings(
            domain = resolveSetting(
                envName = "JWT_DOMAIN",
                defaultValue = defaultJwtDomain
            ),
            audience = resolveSetting(
                envName = "JWT_AUDIENCE",
                defaultValue = defaultJwtAudience
            ),
            realm = resolveSetting(
                envName = "JWT_REALM",
                defaultValue = defaultJwtRealm
            ),
            secret = resolveSetting(
                envName = "JWT_SECRET",
                defaultValue = defaultJwtSecret
            )
        )
    )
}

private fun resolveSetting(
    envName: String,
    configValue: String? = null,
    defaultValue: String
): String {
    val envValue = System.getenv(envName)

    return when {
        !envValue.isNullOrBlank() -> envValue
        !configValue.isNullOrBlank() -> configValue
        else -> defaultValue
    }
}
