package com.expensetracker.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.BudgetWithProgress
import com.expensetracker.app.domain.model.CategorySpending
import com.expensetracker.app.domain.model.MonthlySummary
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class DailySpendPoint(
    val dayOfMonth: Int,
    val amountMinor: Long
)

data class HomeUiState(
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val currencyCode: String = "USD",
    val summary: MonthlySummary = MonthlySummary(
        year = Calendar.getInstance().get(Calendar.YEAR),
        month = Calendar.getInstance().get(Calendar.MONTH) + 1,
        totalSpentMinor = 0L,
        totalIncomeMinor = 0L,
        netCashFlowMinor = 0L,
        currencyCode = "USD"
    ),
    val recentTransactions: List<Transaction> = emptyList(),
    val topCategories: List<CategorySpending> = emptyList(),
    val dailySpendPoints: List<DailySpendPoint> = emptyList(),
    val overallBudget: BudgetWithProgress? = null,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: ExpenseRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.currencyCode.collect { currency ->
                _uiState.update { it.copy(currencyCode = currency) }
                refreshMonthData()
            }
        }
    }

    fun previousMonth() {
        val currentYear = _uiState.value.year
        val currentMonth = _uiState.value.month
        val (newYear, newMonth) = if (currentMonth == 1) {
            Pair(currentYear - 1, 12)
        } else {
            Pair(currentYear, currentMonth - 1)
        }
        _uiState.update { it.copy(year = newYear, month = newMonth) }
        refreshMonthData()
    }

    fun nextMonth() {
        val currentYear = _uiState.value.year
        val currentMonth = _uiState.value.month
        val (newYear, newMonth) = if (currentMonth == 12) {
            Pair(currentYear + 1, 1)
        } else {
            Pair(currentYear, currentMonth + 1)
        }
        _uiState.update { it.copy(year = newYear, month = newMonth) }
        refreshMonthData()
    }

    private fun refreshMonthData() {
        val state = _uiState.value
        val year = state.year
        val month = state.month
        val currency = state.currencyCode

        viewModelScope.launch {
            combine(
                repository.observeMonthlySummary(year, month, currency),
                repository.observeTransactionsForMonth(year, month),
                repository.observeCategoryBreakdown(year, month),
                repository.observeBudgetsWithProgress(year, month)
            ) { summary, transactions, categories, budgets ->
                val recent = transactions.take(5)
                val topCats = categories.take(4)

                // Compute daily spend points for the month
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month - 1)
                val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

                val dayMap = mutableMapOf<Int, Long>()
                for (d in 1..daysInMonth) {
                    dayMap[d] = 0L
                }

                transactions.filter { it.type == TransactionType.EXPENSE }.forEach { tx ->
                    cal.timeInMillis = tx.occurredAt
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    dayMap[day] = (dayMap[day] ?: 0L) + tx.amountMinor
                }

                val dailyPoints = dayMap.entries.sortedBy { it.key }.map {
                    DailySpendPoint(it.key, it.value)
                }

                val overall = budgets.firstOrNull { it.budget.categoryId == null }

                HomeUiData(summary, recent, topCats, dailyPoints, overall)
            }.collect { data ->
                _uiState.update {
                    it.copy(
                        summary = data.summary,
                        recentTransactions = data.recent,
                        topCategories = data.topCats,
                        dailySpendPoints = data.dailyPoints,
                        overallBudget = data.overallBudget,
                        isLoading = false
                    )
                }
            }
        }
    }

    private data class HomeUiData(
        val summary: MonthlySummary,
        val recent: List<Transaction>,
        val topCats: List<CategorySpending>,
        val dailyPoints: List<DailySpendPoint>,
        val overallBudget: BudgetWithProgress?
    )
}

