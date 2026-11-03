package com.expensetracker.app.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.CategorySpending
import com.expensetracker.app.domain.model.MonthlySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class InsightsPeriod(val label: String) {
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month")
}

data class InsightsUiState(
    val period: InsightsPeriod = InsightsPeriod.THIS_MONTH,
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
    val categoryBreakdown: List<CategorySpending> = emptyList(),
    val dailyAverageMinor: Long = 0L,
    val topCategory: CategorySpending? = null,
    val isLoading: Boolean = true
)

class InsightsViewModel(
    private val repository: ExpenseRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.currencyCode.collect { currency ->
                _uiState.update { it.copy(currencyCode = currency) }
                loadPeriodData()
            }
        }
    }

    fun selectPeriod(period: InsightsPeriod) {
        val cal = Calendar.getInstance()
        if (period == InsightsPeriod.LAST_MONTH) {
            cal.add(Calendar.MONTH, -1)
        }
        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH) + 1

        _uiState.update {
            it.copy(
                period = period,
                year = targetYear,
                month = targetMonth
            )
        }
        loadPeriodData()
    }

    private fun loadPeriodData() {
        val state = _uiState.value
        val year = state.year
        val month = state.month
        val currency = state.currencyCode

        viewModelScope.launch {
            combine(
                repository.observeMonthlySummary(year, month, currency),
                repository.observeCategoryBreakdown(year, month)
            ) { summary, breakdown ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month - 1)

                val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val isCurrentMonth = (year == Calendar.getInstance().get(Calendar.YEAR) &&
                        month == Calendar.getInstance().get(Calendar.MONTH) + 1)
                val daysPassed = if (isCurrentMonth) {
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
                } else {
                    daysInMonth
                }

                val dailyAvg = if (daysPassed > 0) summary.totalSpentMinor / daysPassed else 0L
                val topCat = breakdown.firstOrNull()

                InsightsData(summary, breakdown, dailyAvg, topCat)
            }.collect { data ->
                _uiState.update {
                    it.copy(
                        summary = data.summary,
                        categoryBreakdown = data.breakdown,
                        dailyAverageMinor = data.dailyAvg,
                        topCategory = data.topCat,
                        isLoading = false
                    )
                }
            }
        }
    }

    private data class InsightsData(
        val summary: MonthlySummary,
        val breakdown: List<CategorySpending>,
        val dailyAvg: Long,
        val topCat: CategorySpending?
    )
}

