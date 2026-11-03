package com.expensetracker.app.feature.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.money.MoneyFormatter
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.Budget
import com.expensetracker.app.domain.model.BudgetWithProgress
import com.expensetracker.app.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class BudgetDialogState(
    val isOpen: Boolean = false,
    val budgetId: String? = null,
    val selectedCategoryId: String? = null, // null = Overall Budget
    val rawAmount: String = "",
    val amountMinor: Long = 0L,
    val validationError: String? = null
)

data class BudgetsUiState(
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val currencyCode: String = "USD",
    val overallBudget: BudgetWithProgress? = null,
    val categoryBudgets: List<BudgetWithProgress> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val dialogState: BudgetDialogState = BudgetDialogState(),
    val isLoading: Boolean = true
)

class BudgetsViewModel(
    private val repository: ExpenseRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetsUiState())
    val uiState: StateFlow<BudgetsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.currencyCode.collect { currency ->
                _uiState.update { it.copy(currencyCode = currency) }
                loadBudgets()
            }
        }
        viewModelScope.launch {
            repository.observeCategories().collect { categories ->
                _uiState.update { it.copy(availableCategories = categories) }
            }
        }
    }

    private fun loadBudgets() {
        val year = _uiState.value.year
        val month = _uiState.value.month

        viewModelScope.launch {
            repository.observeBudgetsWithProgress(year, month).collect { budgetsWithProgress ->
                val overall = budgetsWithProgress.firstOrNull { it.budget.categoryId == null }
                val catBudgets = budgetsWithProgress.filter { it.budget.categoryId != null }

                _uiState.update {
                    it.copy(
                        overallBudget = overall,
                        categoryBudgets = catBudgets,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun openCreateDialog(categoryId: String? = null) {
        _uiState.update {
            it.copy(
                dialogState = BudgetDialogState(
                    isOpen = true,
                    budgetId = null,
                    selectedCategoryId = categoryId,
                    rawAmount = "",
                    amountMinor = 0L
                )
            )
        }
    }

    fun openEditDialog(budget: BudgetWithProgress) {
        val amountStr = if (budget.budget.amountMinor % 100 == 0L) {
            "${budget.budget.amountMinor / 100}"
        } else {
            String.format("%.2f", budget.budget.amountMinor / 100.0)
        }

        _uiState.update {
            it.copy(
                dialogState = BudgetDialogState(
                    isOpen = true,
                    budgetId = budget.budget.id,
                    selectedCategoryId = budget.budget.categoryId,
                    rawAmount = amountStr,
                    amountMinor = budget.budget.amountMinor
                )
            )
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = BudgetDialogState(isOpen = false)) }
    }

    fun onDialogAmountChanged(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' || it == ',' }
        val minor = MoneyFormatter.parseToMinor(filtered, _uiState.value.currencyCode)
        _uiState.update {
            it.copy(
                dialogState = it.dialogState.copy(
                    rawAmount = filtered,
                    amountMinor = minor,
                    validationError = if (minor <= 0) "Amount must be greater than zero" else null
                )
            )
        }
    }

    fun setDialogCategoryId(categoryId: String?) {
        _uiState.update {
            it.copy(dialogState = it.dialogState.copy(selectedCategoryId = categoryId))
        }
    }

    fun saveBudget() {
        val state = _uiState.value
        val dialog = state.dialogState
        if (dialog.amountMinor <= 0) {
            _uiState.update {
                it.copy(dialogState = dialog.copy(validationError = "Please enter a valid amount"))
            }
            return
        }

        viewModelScope.launch {
            val budget = Budget(
                id = dialog.budgetId ?: UUID.randomUUID().toString(),
                categoryId = dialog.selectedCategoryId,
                amountMinor = dialog.amountMinor,
                currencyCode = state.currencyCode,
                year = state.year,
                month = state.month
            )
            repository.saveBudget(budget)
            dismissDialog()
        }
    }

    fun deleteBudget(id: String) {
        viewModelScope.launch {
            repository.deleteBudget(id)
            dismissDialog()
        }
    }
}

