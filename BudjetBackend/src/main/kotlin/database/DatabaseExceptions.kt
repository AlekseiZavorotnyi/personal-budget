package com.database

import java.util.UUID

class TransactionNotFoundException(id: UUID) : RuntimeException("Transaction $id not found")
