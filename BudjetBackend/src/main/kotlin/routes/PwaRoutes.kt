package com.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.registerPwaRoutes() {
    get("/manifest.json") {
        call.respond(
            PwaManifestResponse(
                name = "Budget PWA",
                shortName = "Budget",
                startUrl = "/",
                display = "standalone",
                backgroundColor = "#ffffff",
                themeColor = "#0F766E",
                icons = listOf(
                    ManifestIcon("/icons/icon-192.png", "192x192", "image/png"),
                    ManifestIcon("/icons/icon-512.png", "512x512", "image/png", "any maskable")
                )
            )
        )
    }

    get("/service-worker.js") {
        call.respondText(
            text = """
                const CACHE_NAME = "budget-pwa-v1";
                self.addEventListener("install", event => {
                  event.waitUntil(caches.open(CACHE_NAME));
                });

                self.addEventListener("fetch", event => {
                  event.respondWith(fetch(event.request).catch(() => caches.match(event.request)));
                });
            """.trimIndent(),
            contentType = ContentType.parse("application/javascript")
        )
    }

    get("/offline.html") {
        call.respondText(
            text = """
                <!doctype html>
                <html lang="ru">
                <head>
                  <meta charset="utf-8" />
                  <title>Budget Offline</title>
                </head>
                <body>
                  <h1>Вы офлайн</h1>
                  <p>Проверьте подключение к интернету и повторите запрос.</p>
                </body>
                </html>
            """.trimIndent(),
            contentType = ContentType.Text.Html
        )
    }
}

@Serializable
private data class ManifestIcon(
    val src: String,
    val sizes: String,
    val type: String,
    val purpose: String? = null
)

@Serializable
private data class PwaManifestResponse(
    val name: String,
    val shortName: String,
    val startUrl: String,
    val display: String,
    val backgroundColor: String,
    val themeColor: String,
    val icons: List<ManifestIcon>
)
