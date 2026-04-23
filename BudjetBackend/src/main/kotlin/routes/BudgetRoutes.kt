package com.routes

import com.mock.BudgetRequest
import com.mock.MockBudgetApi
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerBudgetRoutes() {
    route("/budgets") {
        get {
            call.respond(MockBudgetApi.listBudgets())
        }

        post {
            val request = call.receiveNullable<BudgetRequest>()
            call.respond(HttpStatusCode.Created, MockBudgetApi.createBudget(request))
        }

        patch("/{id}") {
            val request = call.receiveNullable<BudgetRequest>()
            call.respond(MockBudgetApi.updateBudget(call.parameters["id"].orEmpty(), request))
        }

        delete("/{id}") {
            call.respond(MockBudgetApi.deleteBudget(call.parameters["id"].orEmpty()))
        }
    }
}
