package com.expensetracker.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object HomeKey : NavKey
@Serializable data object ActivityKey : NavKey
@Serializable data object InsightsKey : NavKey
@Serializable data object BudgetsKey : NavKey
@Serializable data object SettingsKey : NavKey

@Serializable data class AddTransactionKey(val transactionId: String? = null) : NavKey
@Serializable data object CategoryManagerKey : NavKey
@Serializable data object OnboardingKey : NavKey
