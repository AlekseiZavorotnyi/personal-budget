package com.mock

import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ApiMessage(
    val message: String,
    val mocked: Boolean = true
)

@Serializable
data class RegisterRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)

@Serializable
data class LoginRequest(
    val email: String? = null,
    val password: String? = null
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val currency: String? = null,
    val timezone: String? = null
)

@Serializable
data class CategoryRequest(
    val name: String? = null,
    val type: String? = null,
    val color: String? = null,
    val icon: String? = null
)

@Serializable
data class BudgetRequest(
    val categoryId: Int? = null,
    val limit: Double? = null,
    val period: String? = null
)

@Serializable
data class SyncTransactionChange(
    val localId: String? = null,
    val type: String? = null,
    val amount: Double? = null,
    val categoryId: Int? = null,
    val date: String? = null,
    val syncStatus: String? = null
)

@Serializable
data class SyncPushRequest(
    val transactions: List<SyncTransactionChange> = emptyList(),
    val lastSyncAt: String? = null
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val currency: String,
    val timezone: String,
    val mocked: Boolean = true
)

@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val mocked: Boolean = true
)

@Serializable
data class AuthSessionResponse(
    val user: UserProfileResponse,
    val tokens: TokenPair,
    val mocked: Boolean = true
)

@Serializable
data class MockCategory(
    val id: Int,
    val name: String,
    val type: String,
    val color: String,
    val icon: String
)

@Serializable
data class CategoriesResponse(
    val items: List<MockCategory>,
    val mocked: Boolean = true
)

@Serializable
data class AnalyticsSummaryResponse(
    val income: Double,
    val expense: Double,
    val balance: Double,
    val from: String? = null,
    val to: String? = null,
    val mocked: Boolean = true
)

@Serializable
data class CategoryAnalyticsItem(
    val categoryId: Int,
    val categoryName: String,
    val type: String,
    val total: Double,
    val transactionCount: Int,
    val color: String
)

@Serializable
data class CategoryAnalyticsResponse(
    val items: List<CategoryAnalyticsItem>,
    val from: String? = null,
    val to: String? = null,
    val mocked: Boolean = true
)

@Serializable
data class PeriodAnalyticsItem(
    val period: String,
    val income: Double,
    val expense: Double,
    val balance: Double
)

@Serializable
data class PeriodAnalyticsResponse(
    val grouping: String,
    val items: List<PeriodAnalyticsItem>,
    val from: String? = null,
    val to: String? = null,
    val mocked: Boolean = true
)

@Serializable
data class MockReportResponse(
    val kind: String,
    val period: String,
    val generatedAt: String,
    val summary: AnalyticsSummaryResponse,
    val topCategories: List<CategoryAnalyticsItem>,
    val mocked: Boolean = true
)

@Serializable
data class MockBudget(
    val id: String,
    val categoryId: Int,
    val categoryName: String,
    val limit: Double,
    val period: String,
    val spent: Double,
    val remaining: Double
)

@Serializable
data class BudgetsResponse(
    val items: List<MockBudget>,
    val mocked: Boolean = true
)

@Serializable
data class SyncAppliedTransaction(
    val localId: String,
    val serverId: String,
    val status: String
)

@Serializable
data class SyncPullResponse(
    val serverTime: String,
    val lastSyncAt: String,
    val transactions: List<SyncTransactionChange>,
    val categories: List<MockCategory>,
    val budgets: List<MockBudget>,
    val mocked: Boolean = true
)

@Serializable
data class SyncPushResponse(
    val serverTime: String,
    val receivedTransactions: Int,
    val appliedTransactions: List<SyncAppliedTransaction>,
    val mocked: Boolean = true
)

@Serializable
data class SyncStatusResponse(
    val status: String,
    val lastSyncAt: String,
    val pendingChanges: Int,
    val mocked: Boolean = true
)

@Serializable
data class ManifestIcon(
    val src: String,
    val sizes: String,
    val type: String,
    val purpose: String? = null
)

@Serializable
data class PwaManifestResponse(
    val name: String,
    val shortName: String,
    val startUrl: String,
    val display: String,
    val backgroundColor: String,
    val themeColor: String,
    val icons: List<ManifestIcon>
)

object MockBudgetApi {
    private const val serverTime = "2026-04-26T18:00:00Z"

    private val baseUser = UserProfileResponse(
        id = "user-1",
        name = "Ivan",
        email = "ivan@example.com",
        currency = "RUB",
        timezone = "Europe/Moscow"
    )

    private val categories = listOf(
        MockCategory(1, "Зарплата", "income", "#4CAF50", "salary"),
        MockCategory(2, "Фриланс", "income", "#2196F3", "laptop"),
        MockCategory(3, "Еда", "expense", "#FFAA00", "food"),
        MockCategory(4, "Транспорт", "expense", "#9C27B0", "bus"),
        MockCategory(5, "Жилье", "expense", "#F44336", "home")
    )

    private val budgets = listOf(
        MockBudget("budget-1", 3, "Еда", 15000.0, "monthly", 1200.0, 13800.0),
        MockBudget("budget-2", 4, "Транспорт", 5000.0, "monthly", 650.0, 4350.0)
    )

    fun register(request: RegisterRequest?): AuthSessionResponse {
        return AuthSessionResponse(
            user = baseUser.copy(
                name = request?.name ?: "Ivan",
                email = request?.email ?: "ivan@example.com"
            ),
            tokens = tokenPair("register")
        )
    }

    fun login(request: LoginRequest?): AuthSessionResponse {
        return AuthSessionResponse(
            user = baseUser.copy(email = request?.email ?: baseUser.email),
            tokens = tokenPair("login")
        )
    }

    fun logout(): ApiMessage = ApiMessage("Mock logout completed")

    fun refresh(): TokenPair = tokenPair("refresh")

    fun currentUser(): UserProfileResponse = baseUser

    fun updateProfile(request: UpdateProfileRequest?): UserProfileResponse {
        return baseUser.copy(
            name = request?.name ?: baseUser.name,
            email = request?.email ?: baseUser.email,
            currency = request?.currency ?: baseUser.currency,
            timezone = request?.timezone ?: baseUser.timezone
        )
    }

    fun deleteProfile(): ApiMessage = ApiMessage("Mock account deletion accepted")

    fun listCategories(): CategoriesResponse = CategoriesResponse(categories)

    fun createCategory(request: CategoryRequest?): MockCategory {
        return MockCategory(
            id = 99,
            name = request?.name ?: "Новая категория",
            type = request?.type ?: "expense",
            color = request?.color ?: "#808080",
            icon = request?.icon ?: "folder"
        )
    }

    fun updateCategory(id: String, request: CategoryRequest?): MockCategory {
        return MockCategory(
            id = id.toIntOrNull() ?: 99,
            name = request?.name ?: "Категория $id",
            type = request?.type ?: "expense",
            color = request?.color ?: "#808080",
            icon = request?.icon ?: "folder"
        )
    }

    fun deleteCategory(id: String): ApiMessage = ApiMessage("Mock category $id deleted")

    fun analyticsSummary(from: String?, to: String?): AnalyticsSummaryResponse {
        return AnalyticsSummaryResponse(
            income = 0.0,
            expense = 0.0,
            balance = 0.0,
            from = from,
            to = to
        )
    }

    fun analyticsByCategory(from: String?, to: String?): CategoryAnalyticsResponse {
        return CategoryAnalyticsResponse(
            items = emptyList(),
            from = from,
            to = to
        )
    }

    fun analyticsByPeriod(grouping: String, from: String?, to: String?): PeriodAnalyticsResponse {
        return PeriodAnalyticsResponse(
            grouping = grouping,
            items = emptyList(),
            from = from,
            to = to
        )
    }

    fun monthlyReport(year: Int, month: Int): MockReportResponse {
        val from = "%04d-%02d-01".format(year, month)
        val to = LocalDate.parse(from).withDayOfMonth(LocalDate.parse(from).lengthOfMonth()).toString()

        return MockReportResponse(
            kind = "monthly",
            period = "%04d-%02d".format(year, month),
            generatedAt = serverTime,
            summary = analyticsSummary(from, to),
            topCategories = analyticsByCategory(from, to).items.take(3)
        )
    }

    fun yearlyReport(year: Int): MockReportResponse {
        val from = "$year-01-01"
        val to = "$year-12-31"

        return MockReportResponse(
            kind = "yearly",
            period = year.toString(),
            generatedAt = serverTime,
            summary = analyticsSummary(from, to),
            topCategories = analyticsByCategory(from, to).items.take(5)
        )
    }

    fun listBudgets(): BudgetsResponse = BudgetsResponse(budgets)

    fun createBudget(request: BudgetRequest?): MockBudget {
        val category = categoryById(request?.categoryId ?: 3)

        return MockBudget(
            id = "budget-new",
            categoryId = category.id,
            categoryName = category.name,
            limit = request?.limit ?: 10000.0,
            period = request?.period ?: "monthly",
            spent = 0.0,
            remaining = request?.limit ?: 10000.0
        )
    }

    fun updateBudget(id: String, request: BudgetRequest?): MockBudget {
        val category = categoryById(request?.categoryId ?: 3)
        val limit = request?.limit ?: 10000.0

        return MockBudget(
            id = id,
            categoryId = category.id,
            categoryName = category.name,
            limit = limit,
            period = request?.period ?: "monthly",
            spent = 2500.0,
            remaining = limit - 2500.0
        )
    }

    fun deleteBudget(id: String): ApiMessage = ApiMessage("Mock budget $id deleted")

    fun pullSync(lastSyncAt: String?): SyncPullResponse {
        return SyncPullResponse(
            serverTime = serverTime,
            lastSyncAt = lastSyncAt ?: "2026-04-25T12:00:00Z",
            transactions = emptyList(),
            categories = categories,
            budgets = budgets
        )
    }

    fun pushSync(request: SyncPushRequest?): SyncPushResponse {
        val appliedTransactions = request?.transactions.orEmpty().mapIndexed { index, transaction ->
            SyncAppliedTransaction(
                localId = transaction.localId ?: "tmp-$index",
                serverId = "txn-sync-$index",
                status = transaction.syncStatus ?: "created"
            )
        }

        return SyncPushResponse(
            serverTime = serverTime,
            receivedTransactions = appliedTransactions.size,
            appliedTransactions = appliedTransactions
        )
    }

    fun syncStatus(): SyncStatusResponse {
        return SyncStatusResponse(
            status = "idle",
            lastSyncAt = "2026-04-26T10:15:00Z",
            pendingChanges = 0
        )
    }

    fun manifest(): PwaManifestResponse {
        return PwaManifestResponse(
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
    }

    fun serviceWorker(): String {
        return """
            const CACHE_NAME = "budget-pwa-mock-v1";
            self.addEventListener("install", event => {
              event.waitUntil(caches.open(CACHE_NAME));
            });

            self.addEventListener("fetch", event => {
              event.respondWith(fetch(event.request).catch(() => caches.match(event.request)));
            });
        """.trimIndent()
    }

    fun offlinePage(): String {
        return """
            <!doctype html>
            <html lang="ru">
            <head>
              <meta charset="utf-8" />
              <title>Budget Offline</title>
            </head>
            <body>
              <h1>Вы офлайн</h1>
              <p>Локальный интерфейс должен показать сохраненные данные и очередь синхронизации.</p>
            </body>
            </html>
        """.trimIndent()
    }

    private fun tokenPair(source: String): TokenPair {
        return TokenPair(
            accessToken = "mock-access-token-$source",
            refreshToken = "mock-refresh-token-$source",
            expiresInSeconds = 3600
        )
    }

    private fun categoryById(id: Int): MockCategory {
        return categories.firstOrNull { it.id == id } ?: categories.first()
    }
}
