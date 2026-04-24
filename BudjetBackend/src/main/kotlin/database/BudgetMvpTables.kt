package com.database

import java.math.BigDecimal
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDate
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

enum class TransactionType(val value: String) {
    INCOME("income"),
    EXPENSE("expense");

    companion object {
        fun fromDb(value: String): TransactionType =
            entries.firstOrNull { it.value == value }
                ?: error("Unknown transaction type: $value")
    }
}

object UsersTable : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex("users_email_key")
    val passwordHash = text("password_hash")

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

object CategoriesTable : UUIDTable("categories") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)

    val name = varchar("name", 100)
    val type = customEnumeration(
        name = "type",
        sql = "VARCHAR(20)",
        fromDb = { TransactionType.fromDb(it as String) },
        toDb = { it.value }
    )

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex("categories_user_id_name_type_key", userId, name, type)
        index("idx_categories_user_id", false, userId)
    }
}

object TransactionsTable : UUIDTable("transactions") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val categoryId = optReference("category_id", CategoriesTable, onDelete = ReferenceOption.SET_NULL)

    val type = customEnumeration(
        name = "type",
        sql = "VARCHAR(20)",
        fromDb = { TransactionType.fromDb(it as String) },
        toDb = { it.value }
    )
    val amount = decimal("amount", precision = 12, scale = 2)

    val title = varchar("title", 150).nullable()
    val description = text("description").nullable()

    val transactionDate = date("transaction_date").defaultExpression(CurrentDate)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    init {
        check("transactions_amount_check") { amount greater BigDecimal.ZERO }

        index("idx_transactions_user_id", false, userId)
        index("idx_transactions_user_date", false, userId, transactionDate)
        index("idx_transactions_user_type", false, userId, type)
    }
}
