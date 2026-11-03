package com.expensetracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType

@Entity(
    tableName = "transactions",
    indices = [
        Index("occurredAt"),
        Index("categoryId"),
        Index("type"),
        Index("isDeleted")
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "EXPENSE" or "INCOME"
    val amountMinor: Long,
    val currencyCode: String,
    val categoryId: String,
    val merchant: String?,
    val note: String?,
    val occurredAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val paymentMethod: String?,
    val isRecurring: Boolean = false,
    val attachmentUri: String? = null,
    val isDeleted: Boolean = false,
    val revision: Long = 1L
) {
    fun toDomain(): Transaction = Transaction(
        id = id,
        type = if (type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE,
        amountMinor = amountMinor,
        currencyCode = currencyCode,
        categoryId = categoryId,
        merchant = merchant,
        note = note,
        occurredAt = occurredAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        paymentMethod = paymentMethod,
        isRecurring = isRecurring,
        attachmentUri = attachmentUri,
        isDeleted = isDeleted,
        revision = revision
    )

    companion object {
        fun fromDomain(domain: Transaction): TransactionEntity = TransactionEntity(
            id = domain.id,
            type = domain.type.name,
            amountMinor = domain.amountMinor,
            currencyCode = domain.currencyCode,
            categoryId = domain.categoryId,
            merchant = domain.merchant,
            note = domain.note,
            occurredAt = domain.occurredAt,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            paymentMethod = domain.paymentMethod,
            isRecurring = domain.isRecurring,
            attachmentUri = domain.attachmentUri,
            isDeleted = domain.isDeleted,
            revision = domain.revision
        )
    }
}

