package com.config

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHttp() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }

    install(Compression)
    routing {
        swaggerUI(path = "openapi") {
        }
    }
}
