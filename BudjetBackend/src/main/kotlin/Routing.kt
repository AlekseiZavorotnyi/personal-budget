package com

import com.database.DatabaseFactory
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RootResponse(
    val service: String,
    val status: String
)

@Serializable
data class HealthResponse(
    val status: String,
    val database: String
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(
                RootResponse(
                    service = "BudgetServer",
                    status = "running"
                )
            )
        }

        get("/health") {
            call.respondHealth()
        }

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

        route("/api") {
            route("/auth") {
                post("/register") {
                    val request = call.receiveNullable<RegisterRequest>()
                    call.respond(HttpStatusCode.Created, MockBudgetApi.register(request))
                }

                post("/login") {
                    val request = call.receiveNullable<LoginRequest>()
                    call.respond(MockBudgetApi.login(request))
                }

                post("/logout") {
                    call.respond(MockBudgetApi.logout())
                }

                post("/refresh") {
                    call.respond(MockBudgetApi.refresh())
                }

                get("/me") {
                    call.respond(MockBudgetApi.currentUser())
                }
            }

            route("/users") {
                patch("/me") {
                    val request = call.receiveNullable<UpdateProfileRequest>()
                    call.respond(MockBudgetApi.updateProfile(request))
                }

                delete("/me") {
                    call.respond(MockBudgetApi.deleteProfile())
                }
            }

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

            route("/analytics") {
                get("/summary") {
                    call.respond(
                        MockBudgetApi.analyticsSummary(
                            from = call.request.queryParameters["from"],
                            to = call.request.queryParameters["to"]
                        )
                    )
                }

                get("/by-category") {
                    call.respond(
                        MockBudgetApi.analyticsByCategory(
                            from = call.request.queryParameters["from"],
                            to = call.request.queryParameters["to"]
                        )
                    )
                }

                get("/by-period") {
                    call.respond(
                        MockBudgetApi.analyticsByPeriod(
                            grouping = call.request.queryParameters["period"] ?: "day",
                            from = call.request.queryParameters["from"],
                            to = call.request.queryParameters["to"]
                        )
                    )
                }
            }

            route("/reports") {
                get("/monthly") {
                    val year = call.request.queryParameters["year"]?.toIntOrNull() ?: 2026
                    val month = call.request.queryParameters["month"]?.toIntOrNull() ?: 4
                    call.respond(MockBudgetApi.monthlyReport(year, month))
                }

                get("/yearly") {
                    val year = call.request.queryParameters["year"]?.toIntOrNull() ?: 2026
                    call.respond(MockBudgetApi.yearlyReport(year))
                }
            }

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

            get("/health") {
                call.respondHealth()
            }
        }

        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "budget"))
        }
    }
}

private suspend fun ApplicationCall.respondHealth() {
    val databaseStatus = if (runCatching { DatabaseFactory.ping() }.getOrDefault(false)) {
        "up"
    } else {
        "down"
    }

    respond(
        HealthResponse(
            status = "ok",
            database = databaseStatus
        )
    )
}
