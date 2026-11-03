package com.expensetracker.app

import com.expensetracker.app.domain.model.Budget
import com.expensetracker.app.domain.model.BudgetThresholdState
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculationTest {

    @Test
    fun testNetCashFlowExactMath() {
        val transactions = listOf(
            Transaction(type = TransactionType.INCOME, amountMinor = 500000L, categoryId = "cat_salary"),
            Transaction(type = TransactionType.EXPENSE, amountMinor = 125000L, categoryId = "cat_home"),
            Transaction(type = TransactionType.EXPENSE, amountMinor = 45000L, categoryId = "cat_groceries"),
            Transaction(type = TransactionType.EXPENSE, amountMinor = 15000L, categoryId = "cat_transport")
        )

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amountMinor }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountMinor }
        val net = totalIncome - totalExpense

        assertEquals(500000L, totalIncome)
        assertEquals(185000L, totalExpense)
        assertEquals(315000L, net)
    }

    @Test
    fun testBudgetThresholdTransitions() {
        val limit = 100000L // $1,000.00

        fun calculateThreshold(spent: Long): BudgetThresholdState {
            val ratio = spent.toFloat() / limit.toFloat()
            return when {
                ratio >= 1.0f -> BudgetThresholdState.OVER_100
                ratio >= 0.90f -> BudgetThresholdState.DANGER_90
                ratio >= 0.75f -> BudgetThresholdState.WARNING_75
                else -> BudgetThresholdState.NORMAL
            }
        }

        assertEquals(BudgetThresholdState.NORMAL, calculateThreshold(50000L)) // 50%
        assertEquals(BudgetThresholdState.NORMAL, calculateThreshold(74000L)) // 74%
        assertEquals(BudgetThresholdState.WARNING_75, calculateThreshold(75000L)) // 75%
        assertEquals(BudgetThresholdState.WARNING_75, calculateThreshold(89000L)) // 89%
        assertEquals(BudgetThresholdState.DANGER_90, calculateThreshold(90000L)) // 90%
        assertEquals(BudgetThresholdState.DANGER_90, calculateThreshold(99000L)) // 99%
        assertEquals(BudgetThresholdState.OVER_100, calculateThreshold(100000L)) // 100%
        assertEquals(BudgetThresholdState.OVER_100, calculateThreshold(115000L)) // 115%
    }
}

