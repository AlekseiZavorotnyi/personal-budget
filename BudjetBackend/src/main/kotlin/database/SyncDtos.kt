package com.database

import kotlinx.serialization.Serializable

@Serializable
data class SyncTransactionItem(
    val localId: String? = null,
    val type: String? = null,
    val amount: Double? = null,
    val categoryId: String? = null,
    val date: String? = null,
    val title: String? = null,
    val description: String? = null
)

@Serializable
data class BulkSyncRequest(
    val transactions: List<SyncTransactionItem> = emptyList()
)

@Serializable
data class SyncResultItem(
    val localId: String?,
    val transaction: TransactionResponse?,
    val status: String,
    val error: String? = null
)

@Serializable
data class BulkSyncResponse(
    val synced: List<SyncResultItem>,
    val successCount: Int,
    val errorCount: Int
)
