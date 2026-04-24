package com.database

import java.util.UUID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update as exposedUpdate
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

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

    fun list(ownerId: UUID): List<CategoryRecord> = transaction {
        UsersTable.ensureExistsInCurrentTransaction(ownerId)

        selectAll()
            .where { userId eq EntityID(ownerId, UsersTable) }
            .orderBy(name to SortOrder.ASC)
            .map { it.toCategoryRecord() }
    }

    fun findById(ownerId: UUID, categoryId: UUID): CategoryRecord? = transaction {
        findByIdInCurrentTransaction(ownerId, categoryId)
    }

    fun create(ownerId: UUID, name: String, type: String): CategoryRecord = transaction {
        UsersTable.ensureExistsInCurrentTransaction(ownerId)

        val categoryId = insertAndGetId {
            it[userId] = EntityID(ownerId, UsersTable)
            it[CategoriesTable.name] = name
            it[CategoriesTable.type] = TransactionType.parse(type)
        }.value

        findByIdInCurrentTransaction(ownerId, categoryId)
            ?: error("Created category $categoryId not found")
    }

    fun update(ownerId: UUID, categoryId: UUID, name: String? = null, type: String? = null): CategoryRecord? = transaction {
        val updated = exposedUpdate({
            (CategoriesTable.id eq categoryId) and (CategoriesTable.userId eq EntityID(ownerId, UsersTable))
        }) {
            name?.let { value -> it[CategoriesTable.name] = value }
            type?.let { value -> it[CategoriesTable.type] = TransactionType.parse(value) }
        }

        if (updated == 0) {
            null
        } else {
            findByIdInCurrentTransaction(ownerId, categoryId)
        }
    }

    fun delete(ownerId: UUID, categoryId: UUID): Boolean = transaction {
        deleteWhere {
            (CategoriesTable.id eq categoryId) and (CategoriesTable.userId eq EntityID(ownerId, UsersTable))
        } > 0
    }

    internal fun existsForUserInCurrentTransaction(ownerId: UUID, categoryId: UUID): Boolean {
        return selectAll()
            .where {
                (CategoriesTable.id eq categoryId) and (CategoriesTable.userId eq EntityID(ownerId, UsersTable))
            }
            .empty()
            .not()
    }

    private fun findByIdInCurrentTransaction(ownerId: UUID, categoryId: UUID): CategoryRecord? {
        return selectAll()
            .where {
                (CategoriesTable.id eq categoryId) and (CategoriesTable.userId eq EntityID(ownerId, UsersTable))
            }
            .singleOrNull()
            ?.toCategoryRecord()
    }

    private fun ResultRow.toCategoryRecord(): CategoryRecord {
        return CategoryRecord(
            id = this[CategoriesTable.id].value.toString(),
            userId = this[CategoriesTable.userId].value.toString(),
            name = this[CategoriesTable.name],
            type = this[CategoriesTable.type].value
        )
    }
}
