package com.expensetracker.app.domain.model

import java.util.UUID

enum class TransactionType {
    EXPENSE,
    INCOME
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amountMinor: Long, // positive integer in smallest currency unit (e.g. cents)
    val currencyCode: String = "USD",
    val categoryId: String,
    val merchant: String? = null,
    val note: String? = null,
    val occurredAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val paymentMethod: String? = "Card", // "Cash", "Card", "Bank", "Other"
    val isRecurring: Boolean = false,
    val attachmentUri: String? = null,
    val isDeleted: Boolean = false,
    val revision: Long = 1L
)

