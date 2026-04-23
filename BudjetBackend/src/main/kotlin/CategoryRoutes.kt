package com

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerCategoryRoutes() {
    route("/categories") {
        get {
            call.respond(MockBudgetApi.listCategories())
        }

        post {
            val request = call.receiveNullable<CategoryRequest>()
            call.respond(HttpStatusCode.Created, MockBudgetApi.createCategory(request))
        }

        patch("/{id}") {
            val request = call.receiveNullable<CategoryRequest>()
            call.respond(MockBudgetApi.updateCategory(call.parameters["id"].orEmpty(), request))
        }

        delete("/{id}") {
            call.respond(MockBudgetApi.deleteCategory(call.parameters["id"].orEmpty()))
        }
    }
}
