package com.expensetracker.app.domain.model

enum class BudgetThresholdState {
    NORMAL,
    WARNING_75,
    DANGER_90,
    OVER_100
}

data class MonthlySummary(
    val year: Int,
    val month: Int,
    val totalSpentMinor: Long,
    val totalIncomeMinor: Long,
    val netCashFlowMinor: Long,
    val currencyCode: String = "USD",
    val transactionCount: Int = 0
)

data class CategorySpending(
    val categoryId: String,
    val categoryName: String,
    val iconKey: String,
    val colorHex: String,
    val spentMinor: Long,
    val percentageOfTotal: Float,
    val transactionCount: Int
)

data class BudgetWithProgress(
    val budget: Budget,
    val spentMinor: Long,
    val remainingMinor: Long,
    val progressPercent: Float, // 0f .. 1f+
    val isOverBudget: Boolean,
    val thresholdState: BudgetThresholdState
)

