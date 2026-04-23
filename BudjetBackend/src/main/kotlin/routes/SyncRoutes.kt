package com.routes

import com.mock.MockBudgetApi
import com.mock.SyncPushRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerSyncRoutes() {
    route("/sync") {
        get {
            call.respond(MockBudgetApi.pullSync(call.request.queryParameters["lastSyncAt"]))
        }

        post {
            val request = call.receiveNullable<SyncPushRequest>()
            call.respond(MockBudgetApi.pushSync(request))
        }

        get("/status") {
            call.respond(MockBudgetApi.syncStatus())
        }
    }
}
