package com.expensetracker.app.domain.model

import java.util.UUID

enum class BudgetPeriod {
    MONTHLY
}

data class Budget(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Monthly Budget",
    val amountMinor: Long,
    val currencyCode: String = "USD",
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val categoryId: String? = null, // null indicates overall monthly budget
    val year: Int,
    val month: Int, // 1..12
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

