package com.expensetracker.app.feature.settings

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class SettingsUiState(
    val currencyCode: String = "USD",
    val themeMode: String = "system",
    val isClearDataDialogOpen: Boolean = false,
    val isExporting: Boolean = false
)

sealed interface SettingsEvent {
    data class ShareCsvFile(val fileIntent: Intent) : SettingsEvent
    data class ShowToast(val message: String) : SettingsEvent
}

class SettingsViewModel(
    private val repository: ExpenseRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    val availableCurrencies = listOf(
        "USD" to "US Dollar ($)",
        "EUR" to "Euro (€)",
        "GBP" to "British Pound (£)",
        "INR" to "Indian Rupee (₹)",
        "JPY" to "Japanese Yen (¥)",
        "CAD" to "Canadian Dollar ($)",
        "AUD" to "Australian Dollar ($)"
    )

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.currencyCode,
                preferencesManager.themeMode
            ) { currency, theme ->
                Pair(currency, theme)
            }.collect { (currency, theme) ->
                _uiState.update {
                    it.copy(
                        currencyCode = currency,
                        themeMode = theme
                    )
                }
            }
        }
    }

    fun setCurrency(code: String) {
        viewModelScope.launch {
            preferencesManager.setCurrency(code)
            _events.emit(SettingsEvent.ShowToast("Currency updated to $code"))
        }
    }

    fun setTheme(mode: String) {
        viewModelScope.launch {
            preferencesManager.setTheme(mode)
        }
    }

    fun openClearDataDialog() {
        _uiState.update { it.copy(isClearDataDialogOpen = true) }
    }

    fun dismissClearDataDialog() {
        _uiState.update { it.copy(isClearDataDialogOpen = false) }
    }

    fun confirmClearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            dismissClearDataDialog()
            _events.emit(SettingsEvent.ShowToast("All local transaction and budget data has been cleared."))
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            try {
                val csvContent = repository.exportTransactionsCsv()
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "[APP_NAME] - Transactions Export")
                    putExtra(Intent.EXTRA_TEXT, csvContent)
                }
                val chooser = Intent.createChooser(sendIntent, "Export Transactions")
                _events.emit(SettingsEvent.ShareCsvFile(chooser))
            } catch (e: Exception) {
                _events.emit(SettingsEvent.ShowToast("Failed to export: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isExporting = false) }
            }
        }
    }
}

