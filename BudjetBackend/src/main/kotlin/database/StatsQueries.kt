package com.database

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.UUID
import kotlin.math.roundToLong

object StatsQueries {

    fun summary(ownerId: UUID, from: String?, to: String?): StatsSummaryResponse = transaction {
        UsersTable.ensureExistsInCurrentTransaction(ownerId)

        val conditions = baseConditions(ownerId, from, to)
        var totalIncome = 0.0
        var totalExpenses = 0.0
        var count = 0L

        TransactionsTable
            .selectAll()
            .where(conditions.compoundAnd())
            .forEach { row ->
                count++
                val amount = row[TransactionsTable.amount].toDouble()
                when (row[TransactionsTable.type]) {
                    TransactionType.INCOME -> totalIncome += amount
                    TransactionType.EXPENSE -> totalExpenses += amount
                }
            }

        StatsSummaryResponse(
            period = StatsPeriod(from = from, to = to),
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            balance = totalIncome - totalExpenses,
            transactionCount = count
        )
    }

    fun byCategory(ownerId: UUID, from: String?, to: String?, type: String?): CategoryStatsResponse = transaction {
        UsersTable.ensureExistsInCurrentTransaction(ownerId)

        val conditions = baseConditions(ownerId, from, to)
        if (type != null) conditions += TransactionsTable.type eq TransactionType.parse(type)

        data class Acc(val name: String?, var total: Double = 0.0, var count: Long = 0L)
        val byCat = LinkedHashMap<String?, Acc>()

        TransactionsTable
            .leftJoin(CategoriesTable)
            .selectAll()
            .where(conditions.compoundAnd())
            .forEach { row ->
                val catId = row[TransactionsTable.categoryId]?.value?.toString()
                val catName = row.getOrNull(CategoriesTable.name)
                val acc = byCat.getOrPut(catId) { Acc(catName) }
                acc.total += row[TransactionsTable.amount].toDouble()
                acc.count++
            }

        val grandTotal = byCat.values.sumOf { it.total }

        val items = byCat.entries
            .map { (catId, acc) ->
                CategoryStatsItem(
                    categoryId = catId,
                    categoryName = acc.name,
                    total = acc.total,
                    transactionCount = acc.count,
                    percentage = if (grandTotal > 0)
                        (acc.total / grandTotal * 10000).roundToLong() / 100.0
                    else
                        0.0
                )
            }
            .sortedByDescending { it.total }

        CategoryStatsResponse(
            period = StatsPeriod(from = from, to = to),
            type = type,
            total = grandTotal,
            items = items
        )
    }

    fun monthly(ownerId: UUID, year: Int): MonthlyStatsResponse = transaction {
        UsersTable.ensureExistsInCurrentTransaction(ownerId)

        val yearStart = LocalDate.of(year, 1, 1)
        val yearEnd = LocalDate.of(year, 12, 31)
        // index 0 = January: [income, expense]
        val totals = Array(12) { doubleArrayOf(0.0, 0.0) }

        TransactionsTable
            .selectAll()
            .where {
                (TransactionsTable.userId eq EntityID(ownerId, UsersTable)) and
                (TransactionsTable.transactionDate greaterEq yearStart) and
                (TransactionsTable.transactionDate lessEq yearEnd)
            }
            .forEach { row ->
                val idx = row[TransactionsTable.transactionDate].monthValue - 1
                val amount = row[TransactionsTable.amount].toDouble()
                when (row[TransactionsTable.type]) {
                    TransactionType.INCOME -> totals[idx][0] += amount
                    TransactionType.EXPENSE -> totals[idx][1] += amount
                }
            }

        val monthNames = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        MonthlyStatsResponse(
            year = year,
            months = monthNames.mapIndexed { i, name ->
                MonthlyStatsItem(
                    month = i + 1,
                    label = name,
                    totalIncome = totals[i][0],
                    totalExpenses = totals[i][1],
                    balance = totals[i][0] - totals[i][1]
                )
            }
        )
    }

    private fun baseConditions(
        ownerId: UUID,
        from: String?,
        to: String?
    ): MutableList<Op<Boolean>> {
        val conditions = mutableListOf<Op<Boolean>>(
            TransactionsTable.userId eq EntityID(ownerId, UsersTable)
        )
        if (from != null) conditions += TransactionsTable.transactionDate greaterEq parseDate(from, "from")
        if (to != null) conditions += TransactionsTable.transactionDate lessEq parseDate(to, "to")
        return conditions
    }

    private fun parseDate(value: String, field: String): LocalDate =
        runCatching { LocalDate.parse(value) }
            .getOrElse { throw IllegalArgumentException("$field must use ISO date format yyyy-MM-dd") }
}
