package com.routes

import com.mock.MockBudgetApi
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerPwaRoutes() {
    get("/manifest.json") {
        call.respond(MockBudgetApi.manifest())
    }

    get("/service-worker.js") {
        call.respondText(
            text = MockBudgetApi.serviceWorker(),
            contentType = ContentType.parse("application/javascript")
        )
    }

    get("/offline.html") {
        call.respondText(
            text = MockBudgetApi.offlinePage(),
            contentType = ContentType.Text.Html
        )
    }
}
