package com

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerTransactionRoutes() {
    route("/transactions") {
        get {
            val filter = TransactionFilterResponse(
                type = call.request.queryParameters["type"],
                categoryId = call.request.queryParameters["categoryId"]?.toIntOrNull(),
                from = call.request.queryParameters["from"],
                to = call.request.queryParameters["to"]
            )

            call.respond(MockBudgetApi.listTransactions(filter))
        }

        get("/{id}") {
            call.respond(MockBudgetApi.getTransaction(call.parameters["id"].orEmpty()))
        }

        post {
            val request = call.receiveNullable<TransactionRequest>()
            call.respond(HttpStatusCode.Created, MockBudgetApi.createTransaction(request))
        }

        patch("/{id}") {
            val request = call.receiveNullable<TransactionRequest>()
            call.respond(MockBudgetApi.updateTransaction(call.parameters["id"].orEmpty(), request))
        }

        delete("/{id}") {
            call.respond(MockBudgetApi.deleteTransaction(call.parameters["id"].orEmpty()))
        }
    }
}
