package com.expensetracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.expensetracker.app.domain.model.Budget
import com.expensetracker.app.domain.model.BudgetPeriod

@Entity(
    tableName = "budgets",
    indices = [
        Index("year", "month"),
        Index("categoryId")
    ]
)
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val amountMinor: Long,
    val currencyCode: String,
    val period: String = "MONTHLY",
    val categoryId: String?, // null indicates overall budget
    val year: Int,
    val month: Int,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Budget = Budget(
        id = id,
        name = name,
        amountMinor = amountMinor,
        currencyCode = currencyCode,
        period = try { BudgetPeriod.valueOf(period) } catch (e: Exception) { BudgetPeriod.MONTHLY },
        categoryId = categoryId,
        year = year,
        month = month,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(domain: Budget): BudgetEntity = BudgetEntity(
            id = domain.id,
            name = domain.name,
            amountMinor = domain.amountMinor,
            currencyCode = domain.currencyCode,
            period = domain.period.name,
            categoryId = domain.categoryId,
            year = domain.year,
            month = domain.month,
            isActive = domain.isActive,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}

