package com.expensetracker.app.feature.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.Category
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ActivityDateFilter(val label: String) {
    ALL("All Time"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month")
}

data class ActivityUiState(
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedCategoryId: String? = null,
    val selectedDateFilter: ActivityDateFilter = ActivityDateFilter.ALL,
    val selectedPaymentMethod: String? = null,
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val filteredCount: Int = 0,
    val totalExpenseMinor: Long = 0L,
    val totalIncomeMinor: Long = 0L,
    val currencyCode: String = "USD",
    val availableCategories: List<Category> = emptyList(),
    val availablePaymentMethods: List<String> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ActivityEvent {
    data class ShowUndoSnackbar(val transaction: Transaction, val message: String) : ActivityEvent
}

class ActivityViewModel(
    private val repository: ExpenseRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ActivityEvent>()
    val events: SharedFlow<ActivityEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            preferencesManager.currencyCode.collect { currency ->
                _uiState.update { it.copy(currencyCode = currency) }
            }
        }

        viewModelScope.launch {
            repository.observeCategories().collect { categories ->
                _uiState.update { it.copy(availableCategories = categories) }
            }
        }

        viewModelScope.launch {
            combine(
                repository.observeAllTransactions(),
                _uiState
            ) { allTransactions, state ->
                // Filter transactions
                val now = System.currentTimeMillis()
                val cal = Calendar.getInstance()

                val (startFilter, endFilter) = when (state.selectedDateFilter) {
                    ActivityDateFilter.ALL -> Pair(0L, Long.MAX_VALUE)
                    ActivityDateFilter.THIS_MONTH -> {
                        DatePeriodUtil.getMonthRange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
                    }
                    ActivityDateFilter.LAST_MONTH -> {
                        cal.add(Calendar.MONTH, -1)
                        val range = DatePeriodUtil.getMonthRange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
                        cal.timeInMillis = now // reset
                        range
                    }
                }

                val filtered = allTransactions.filter { tx ->
                    val matchesQuery = if (state.searchQuery.isBlank()) true else {
                        val q = state.searchQuery.trim().lowercase()
                        val merchantMatch = tx.merchant?.lowercase()?.contains(q) == true
                        val noteMatch = tx.note?.lowercase()?.contains(q) == true
                        val catMatch = tx.categoryId.lowercase().contains(q)
                        merchantMatch || noteMatch || catMatch
                    }

                    val matchesType = state.selectedType == null || tx.type == state.selectedType
                    val matchesCategory = state.selectedCategoryId == null || tx.categoryId == state.selectedCategoryId
                    val matchesPayment = state.selectedPaymentMethod == null || tx.paymentMethod == state.selectedPaymentMethod
                    val matchesDate = tx.occurredAt in startFilter..endFilter

                    matchesQuery && matchesType && matchesCategory && matchesPayment && matchesDate
                }

                // Group by section: "Today", "Yesterday", or formatted date
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val yesterdayStart = todayStart - 86_400_000L

                val grouped = filtered.groupBy { tx ->
                    when {
                        tx.occurredAt >= todayStart -> "Today"
                        tx.occurredAt >= yesterdayStart -> "Yesterday"
                        else -> DatePeriodUtil.formatMediumDate(tx.occurredAt)
                    }
                }

                val expenseSum = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountMinor }
                val incomeSum = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amountMinor }

                val methods = allTransactions.mapNotNull { it.paymentMethod?.trim() }.filter { it.isNotEmpty() }.distinct()

                Triple(grouped, Pair(expenseSum, incomeSum), methods to filtered.size)
            }.collect { (grouped, totals, methodsAndSize) ->
                _uiState.update {
                    it.copy(
                        groupedTransactions = grouped,
                        totalExpenseMinor = totals.first,
                        totalIncomeMinor = totals.second,
                        availablePaymentMethods = methodsAndSize.first,
                        filteredCount = methodsAndSize.second,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setTypeFilter(type: TransactionType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun setCategoryFilter(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setDateFilter(filter: ActivityDateFilter) {
        _uiState.update { it.copy(selectedDateFilter = filter) }
    }

    fun setPaymentMethodFilter(method: String?) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction.id, soft = true)
            _events.emit(ActivityEvent.ShowUndoSnackbar(transaction, "Transaction deleted"))
        }
    }

    fun undoDelete(transaction: Transaction) {
        viewModelScope.launch {
            repository.restoreTransaction(transaction.id)
        }
    }
}

