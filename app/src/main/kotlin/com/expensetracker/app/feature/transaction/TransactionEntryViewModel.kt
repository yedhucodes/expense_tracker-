package com.expensetracker.app.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.money.MoneyFormatter
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class TransactionEntryUiState(
    val isEditing: Boolean = false,
    val transactionId: String = UUID.randomUUID().toString(),
    val type: TransactionType = TransactionType.EXPENSE,
    val rawAmount: String = "",
    val amountMinor: Long = 0L,
    val currencyCode: String = "USD",
    val selectedCategoryId: String = "cat_food",
    val occurredAt: Long = System.currentTimeMillis(),
    val merchant: String = "",
    val note: String = "",
    val paymentMethod: String = "Card",
    val availableCategories: List<Category> = emptyList(),
    val validationError: String? = null,
    val isSaving: Boolean = false
)

sealed interface TransactionEntryEvent {
    data object SaveSuccess : TransactionEntryEvent
    data class ShowToast(val message: String) : TransactionEntryEvent
}

class TransactionEntryViewModel(
    private val repository: ExpenseRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionEntryUiState())
    val uiState: StateFlow<TransactionEntryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransactionEntryEvent>()
    val events: SharedFlow<TransactionEntryEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val currency = preferencesManager.currencyCode.first()
            _uiState.update { it.copy(currencyCode = currency) }
        }
        viewModelScope.launch {
            repository.observeCategories().collect { categories ->
                _uiState.update { state ->
                    val filtered = categories.filter {
                        if (state.type == TransactionType.EXPENSE) it.type != com.expensetracker.app.core.design.category.CategoryType.INCOME
                        else it.type != com.expensetracker.app.core.design.category.CategoryType.EXPENSE
                    }
                    val currentCatValid = filtered.any { it.id == state.selectedCategoryId }
                    val newSelectedCat = if (currentCatValid) state.selectedCategoryId else filtered.firstOrNull()?.id ?: "cat_other_expense"

                    state.copy(
                        availableCategories = filtered,
                        selectedCategoryId = newSelectedCat
                    )
                }
            }
        }
    }

    fun loadTransaction(id: String) {
        viewModelScope.launch {
            repository.getTransactionById(id).collect { tx ->
                if (tx != null) {
                    val amountStr = if (tx.amountMinor % 100 == 0L) {
                        "${tx.amountMinor / 100}"
                    } else {
                        String.format("%.2f", tx.amountMinor / 100.0)
                    }

                    _uiState.update {
                        it.copy(
                            isEditing = true,
                            transactionId = tx.id,
                            type = tx.type,
                            rawAmount = amountStr,
                            amountMinor = tx.amountMinor,
                            currencyCode = tx.currencyCode,
                            selectedCategoryId = tx.categoryId,
                            occurredAt = tx.occurredAt,
                            merchant = tx.merchant ?: "",
                            note = tx.note ?: "",
                            paymentMethod = tx.paymentMethod ?: "Card"
                        )
                    }
                }
            }
        }
    }

    fun setType(type: TransactionType) {
        _uiState.update { state ->
            val defaultCat = if (type == TransactionType.EXPENSE) "cat_food" else "cat_salary"
            state.copy(
                type = type,
                selectedCategoryId = defaultCat
            )
        }
        // Refresh category filter
        viewModelScope.launch {
            val categories = repository.observeCategories().first()
            val filtered = categories.filter {
                if (type == TransactionType.EXPENSE) it.type != com.expensetracker.app.core.design.category.CategoryType.INCOME
                else it.type != com.expensetracker.app.core.design.category.CategoryType.EXPENSE
            }
            _uiState.update { it.copy(availableCategories = filtered) }
        }
    }

    fun onAmountChanged(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' || it == ',' }
        val minor = MoneyFormatter.parseToMinor(filtered, _uiState.value.currencyCode)
        _uiState.update {
            it.copy(
                rawAmount = filtered,
                amountMinor = minor,
                validationError = if (minor <= 0) "Amount must be greater than zero" else null
            )
        }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setDate(epochMillis: Long) {
        _uiState.update { it.copy(occurredAt = epochMillis) }
    }

    fun setMerchant(merchant: String) {
        _uiState.update { it.copy(merchant = merchant) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun setPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun save() {
        val state = _uiState.value
        if (state.amountMinor <= 0) {
            _uiState.update { it.copy(validationError = "Please enter a valid amount greater than 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val transaction = Transaction(
                id = state.transactionId,
                type = state.type,
                amountMinor = state.amountMinor,
                currencyCode = state.currencyCode,
                categoryId = state.selectedCategoryId,
                merchant = state.merchant.trim().ifEmpty { null },
                note = state.note.trim().ifEmpty { null },
                occurredAt = state.occurredAt,
                paymentMethod = state.paymentMethod
            )
            repository.saveTransaction(transaction)
            _uiState.update { it.copy(isSaving = false) }
            _events.emit(TransactionEntryEvent.SaveSuccess)
        }
    }

    fun deleteCurrent(onDeleted: () -> Unit) {
        val state = _uiState.value
        if (!state.isEditing) return
        viewModelScope.launch {
            repository.deleteTransaction(state.transactionId, soft = true)
            onDeleted()
        }
    }
}

