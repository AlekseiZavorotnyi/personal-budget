package com.database

enum class TransactionType(val value: String) {
    INCOME("income"),
    EXPENSE("expense");

    companion object {
        fun fromDb(value: String): TransactionType =
            entries.firstOrNull { it.value == value }
                ?: error("Unknown transaction type: $value")

        fun parse(value: String?): TransactionType {
            require(!value.isNullOrBlank()) { "type is required" }

            return entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("type must be income or expense")
        }
    }
}
