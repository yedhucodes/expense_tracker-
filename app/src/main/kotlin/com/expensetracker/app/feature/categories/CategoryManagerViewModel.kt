package com.expensetracker.app.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.category.CategoryType
import com.expensetracker.app.core.design.category.CuratedPalette
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.Category
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CreateCategoryState(
    val isOpen: Boolean = false,
    val name: String = "",
    val iconKey: String = "category",
    val colorHex: String = CuratedPalette.first(),
    val type: CategoryType = CategoryType.EXPENSE,
    val validationError: String? = null
)

data class ReassignDeleteState(
    val isOpen: Boolean = false,
    val categoryToDelete: Category? = null,
    val targetCategoryId: String? = null,
    val affectedTransactionCount: Int = 0
)

data class CategoryManagerUiState(
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val createState: CreateCategoryState = CreateCategoryState(),
    val reassignState: ReassignDeleteState = ReassignDeleteState(),
    val isLoading: Boolean = true
)

sealed interface CategoryManagerEvent {
    data class ShowToast(val message: String) : CategoryManagerEvent
}

class CategoryManagerViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagerUiState())
    val uiState: StateFlow<CategoryManagerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CategoryManagerEvent>()
    val events: SharedFlow<CategoryManagerEvent> = _events.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.observeCategories().collect { categories ->
                val expense = categories.filter { it.type != CategoryType.INCOME }
                val income = categories.filter { it.type == CategoryType.INCOME }
                _uiState.update {
                    it.copy(
                        expenseCategories = expense,
                        incomeCategories = income,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun openCreateDialog(type: CategoryType = CategoryType.EXPENSE) {
        _uiState.update {
            it.copy(
                createState = CreateCategoryState(
                    isOpen = true,
                    type = type,
                    colorHex = CuratedPalette.first(),
                    iconKey = if (type == CategoryType.EXPENSE) "shopping" else "wallet"
                )
            )
        }
    }

    fun dismissCreateDialog() {
        _uiState.update { it.copy(createState = CreateCategoryState(isOpen = false)) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(createState = it.createState.copy(name = name, validationError = null)) }
    }

    fun onIconSelected(iconKey: String) {
        _uiState.update { it.copy(createState = it.createState.copy(iconKey = iconKey)) }
    }

    fun onColorSelected(hex: String) {
        _uiState.update { it.copy(createState = it.createState.copy(colorHex = hex)) }
    }

    fun onTypeChanged(type: CategoryType) {
        _uiState.update { it.copy(createState = it.createState.copy(type = type)) }
    }

    fun saveCategory() {
        val create = _uiState.value.createState
        val trimmed = create.name.trim()
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(createState = create.copy(validationError = "Category name cannot be empty")) }
            return
        }

        viewModelScope.launch {
            val newCategory = Category(
                id = "cat_custom_${UUID.randomUUID()}",
                name = trimmed,
                iconKey = create.iconKey,
                colorHex = create.colorHex,
                type = create.type,
                isBuiltIn = false,
                sortOrder = 999
            )
            repository.saveCategory(newCategory)
            dismissCreateDialog()
            _events.emit(CategoryManagerEvent.ShowToast("Category created"))
        }
    }

    fun requestDeleteCategory(category: Category) {
        if (category.isBuiltIn) {
            viewModelScope.launch {
                _events.emit(CategoryManagerEvent.ShowToast("Built-in categories cannot be deleted"))
            }
            return
        }

        viewModelScope.launch {
            val count = repository.countTransactionsForCategory(category.id)
            if (count > 0) {
                // Requires reassignment
                val fallbackTargets = if (category.type == CategoryType.INCOME) {
                    _uiState.value.incomeCategories.filter { it.id != category.id }
                } else {
                    _uiState.value.expenseCategories.filter { it.id != category.id }
                }
                _uiState.update {
                    it.copy(
                        reassignState = ReassignDeleteState(
                            isOpen = true,
                            categoryToDelete = category,
                            targetCategoryId = fallbackTargets.firstOrNull()?.id ?: "cat_other_expense",
                            affectedTransactionCount = count
                        )
                    )
                }
            } else {
                // Safe to delete directly
                repository.archiveCategory(category.id)
                _events.emit(CategoryManagerEvent.ShowToast("Category deleted"))
            }
        }
    }

    fun setReassignTarget(targetId: String) {
        _uiState.update { it.copy(reassignState = it.reassignState.copy(targetCategoryId = targetId)) }
    }

    fun dismissReassignDialog() {
        _uiState.update { it.copy(reassignState = ReassignDeleteState(isOpen = false)) }
    }

    fun confirmReassignAndDelete() {
        val reassign = _uiState.value.reassignState
        val toDelete = reassign.categoryToDelete ?: return
        val target = reassign.targetCategoryId ?: return

        viewModelScope.launch {
            repository.reassignCategory(toDelete.id, target)
            repository.archiveCategory(toDelete.id)
            dismissReassignDialog()
            _events.emit(CategoryManagerEvent.ShowToast("Transactions reassigned and category deleted"))
        }
    }
}

