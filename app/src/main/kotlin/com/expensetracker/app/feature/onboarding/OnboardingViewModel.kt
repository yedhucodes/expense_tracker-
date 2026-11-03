package com.expensetracker.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.money.MoneyFormatter
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class OnboardingUiState(
    val selectedCurrency: String = "USD",
    val initialBudgetRaw: String = "",
    val initialBudgetMinor: Long = 0L,
    val isCompleting: Boolean = false
)

class OnboardingViewModel(
    private val preferencesManager: UserPreferencesManager,
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val availableCurrencies = listOf(
        "USD" to "US Dollar ($)",
        "EUR" to "Euro (€)",
        "GBP" to "British Pound (£)",
        "INR" to "Indian Rupee (₹)",
        "JPY" to "Japanese Yen (¥)",
        "CAD" to "Canadian Dollar ($)",
        "AUD" to "Australian Dollar ($)"
    )

    fun selectCurrency(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    fun onBudgetChanged(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' || it == ',' }
        val minor = MoneyFormatter.parseToMinor(filtered, _uiState.value.selectedCurrency)
        _uiState.update {
            it.copy(
                initialBudgetRaw = filtered,
                initialBudgetMinor = minor
            )
        }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCompleting = true) }
            val state = _uiState.value
            preferencesManager.setCurrency(state.selectedCurrency)

            if (state.initialBudgetMinor > 0) {
                val cal = Calendar.getInstance()
                val budget = Budget(
                    id = UUID.randomUUID().toString(),
                    categoryId = null, // Overall budget
                    amountMinor = state.initialBudgetMinor,
                    currencyCode = state.selectedCurrency,
                    year = cal.get(Calendar.YEAR),
                    month = cal.get(Calendar.MONTH) + 1
                )
                repository.saveBudget(budget)
            }

            preferencesManager.setOnboardingCompleted(true)
            _uiState.update { it.copy(isCompleting = false) }
            onSuccess()
        }
    }
}

