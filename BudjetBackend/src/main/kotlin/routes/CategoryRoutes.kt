package com.routes

import com.database.ApiMessage
import com.database.CategoriesResponse
import com.database.CategoryRequest
import com.database.CategoriesTable
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.registerCategoryRoutes() {
    authenticate {
        route("/categories") {
            get {
                call.respond(CategoriesResponse(CategoriesTable.list(call.requiredUserId())))
            }

            post {
                val request = call.receiveNullable<CategoryRequest>()
                    ?: throw IllegalArgumentException("Request body is required")
                call.respond(HttpStatusCode.Created, CategoriesTable.create(call.requiredUserId(), request))
            }

            patch("/{id}") {
                val request = call.receiveNullable<CategoryRequest>()
                    ?: throw IllegalArgumentException("Request body is required")
                val category = CategoriesTable.update(
                    ownerId = call.requiredUserId(),
                    categoryId = call.requiredUuidParameter("id"),
                    request = request
                )

                if (category == null) {
                    call.respond(HttpStatusCode.NotFound, ApiMessage("Category not found"))
                } else {
                    call.respond(category)
                }
            }

            delete("/{id}") {
                val deleted = CategoriesTable.delete(
                    ownerId = call.requiredUserId(),
                    categoryId = call.requiredUuidParameter("id")
                )

                if (deleted) {
                    call.respond(ApiMessage("Category deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiMessage("Category not found"))
                }
            }
        }
    }
}

private fun ApplicationCall.requiredUuidParameter(name: String): UUID {
    val value = parameters[name]?.takeIf(String::isNotBlank)
        ?: throw IllegalArgumentException("$name is required")

    return runCatching { UUID.fromString(value) }
        .getOrElse { throw IllegalArgumentException("$name must be a valid UUID") }
}
