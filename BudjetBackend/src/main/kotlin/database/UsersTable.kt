package com.database

import java.time.LocalDateTime
import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update as exposedUpdate
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : UUIDTable("users") {
    private val fallbackUserId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

    val email = varchar("email", 255).uniqueIndex("users_email_key")
    val passwordHash = text("password_hash")
    val name = varchar("name", 100).nullable()
    val currency = varchar("currency", 16).default("RUB")
    val timezone = varchar("timezone", 64).default("Europe/Moscow")

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    fun defaultUserId(): UUID = fallbackUserId

    fun create(email: String, passwordHash: String, name: String): UserRecord = transaction {
        val userId = insertAndGetId {
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.name] = name
        }.value

        findByIdInCurrentTransaction(userId) ?: error("Created user $userId not found")
    }

    fun findById(userId: UUID): UserRecord? = transaction {
        findByIdInCurrentTransaction(userId)
    }

    fun findByEmail(email: String): UserRecord? = transaction {
        selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?.toUserRecord()
    }

    fun findByEmailWithPassword(email: String): UserCredentialsRecord? = transaction {
        selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?.toUserCredentialsRecord()
    }

    fun updatePassword(userId: UUID, passwordHash: String): Boolean = transaction {
        exposedUpdate({ UsersTable.id eq userId }) {
            it[UsersTable.passwordHash] = passwordHash
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }

    fun delete(userId: UUID): Boolean = transaction {
        deleteWhere { UsersTable.id eq userId } > 0
    }

    fun ensureExists(userId: UUID): UserRecord = transaction {
        ensureExistsInCurrentTransaction(userId)
        findByIdInCurrentTransaction(userId) ?: error("User $userId not found")
    }

    internal fun ensureExistsInCurrentTransaction(userId: UUID) {
        insertIgnore {
            it[id] = EntityID(userId, UsersTable)
            it[email] = "$userId@local.budget"
            it[passwordHash] = "external-or-demo-user"
            it[name] = "Demo User"
        }
    }

    private fun findByIdInCurrentTransaction(userId: UUID): UserRecord? {
        return selectAll()
            .where { UsersTable.id eq userId }
            .singleOrNull()
            ?.toUserRecord()
    }

    private fun ResultRow.toUserRecord(): UserRecord {
        return UserRecord(
            id = this[UsersTable.id].value.toString(),
            email = this[email],
            name = this[name] ?: this[email].substringBefore("@"),
            currency = this[currency],
            timezone = this[timezone]
        )
    }

    private fun ResultRow.toUserCredentialsRecord(): UserCredentialsRecord {
        return UserCredentialsRecord(
            id = this[UsersTable.id].value.toString(),
            email = this[email],
            name = this[name] ?: this[email].substringBefore("@"),
            currency = this[currency],
            timezone = this[timezone],
            passwordHash = this[passwordHash]
        )
    }
}
