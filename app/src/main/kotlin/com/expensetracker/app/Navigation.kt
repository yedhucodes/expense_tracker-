package com.expensetracker.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.expensetracker.app.core.design.components.GlassNavigationBar
import com.expensetracker.app.core.design.components.NavDestination
import com.expensetracker.app.core.design.theme.LocalHazeState
import dev.chrisbanes.haze.HazeState
import com.expensetracker.app.data.local.AppDatabase
import com.expensetracker.app.data.local.UserPreferencesManager
import com.expensetracker.app.data.repository.ExpenseRepository
import com.expensetracker.app.domain.model.TransactionType
import com.expensetracker.app.feature.activity.ActivityScreen
import com.expensetracker.app.feature.activity.ActivityViewModel
import com.expensetracker.app.feature.budgets.BudgetsScreen
import com.expensetracker.app.feature.budgets.BudgetsViewModel
import com.expensetracker.app.feature.categories.CategoryManagerScreen
import com.expensetracker.app.feature.categories.CategoryManagerViewModel
import com.expensetracker.app.feature.home.HomeScreen
import com.expensetracker.app.feature.home.HomeViewModel
import com.expensetracker.app.feature.insights.InsightsScreen
import com.expensetracker.app.feature.insights.InsightsViewModel
import com.expensetracker.app.feature.onboarding.OnboardingScreen
import com.expensetracker.app.feature.onboarding.OnboardingViewModel
import com.expensetracker.app.feature.settings.SettingsScreen
import com.expensetracker.app.feature.settings.SettingsViewModel
import com.expensetracker.app.feature.transaction.AddTransactionScreen
import com.expensetracker.app.feature.transaction.TransactionEntryViewModel

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val preferencesManager = remember { UserPreferencesManager(context) }
    val repository = remember { ExpenseRepository(database) }

    val homeViewModel = remember { HomeViewModel(repository, preferencesManager) }
    val activityViewModel = remember { ActivityViewModel(repository, preferencesManager) }
    val insightsViewModel = remember { InsightsViewModel(repository, preferencesManager) }
    val budgetsViewModel = remember { BudgetsViewModel(repository, preferencesManager) }
    val settingsViewModel = remember { SettingsViewModel(repository, preferencesManager) }
    val categoryManagerViewModel = remember { CategoryManagerViewModel(repository) }
    val onboardingViewModel = remember { OnboardingViewModel(preferencesManager, repository) }
    val transactionEntryViewModel = remember { TransactionEntryViewModel(repository, preferencesManager) }

    val onboardingCompleted by preferencesManager.onboardingCompleted.collectAsState(initial = true)

    val backStack = rememberNavBackStack(if (onboardingCompleted) HomeKey else OnboardingKey)

    // Current top-level destination tracker for bottom nav
    val currentKey = backStack.lastOrNull()
    val isTopLevelDestination = currentKey is HomeKey || currentKey is ActivityKey || currentKey is InsightsKey || currentKey is BudgetsKey

    // Ensure system back gesture / back button always returns to Home before exiting the app
    BackHandler(enabled = isTopLevelDestination && currentKey !is HomeKey) {
        backStack.clear()
        backStack.add(HomeKey)
    }

    val currentNavDestination = when (currentKey) {
        is ActivityKey -> NavDestination.Activity
        is InsightsKey -> NavDestination.Insights
        is BudgetsKey -> NavDestination.Budgets
        else -> NavDestination.Home
    }

    val hazeState = remember { HazeState() }

    CompositionLocalProvider(LocalHazeState provides hazeState) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavDisplay(
                backStack = backStack,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                },
            entryProvider = entryProvider {
                entry<HomeKey> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToAddTransaction = {
                            transactionEntryViewModel.setType(TransactionType.EXPENSE)
                            backStack.add(AddTransactionKey(null))
                        },
                        onNavigateToTransactionDetail = { id ->
                            transactionEntryViewModel.loadTransaction(id)
                            backStack.add(AddTransactionKey(id))
                        },
                        onNavigateToActivity = {
                            backStack.clear()
                            backStack.add(HomeKey)
                            backStack.add(ActivityKey)
                        },
                        onNavigateToBudgets = {
                            backStack.clear()
                            backStack.add(HomeKey)
                            backStack.add(BudgetsKey)
                        },
                        onNavigateToSettings = {
                            backStack.add(SettingsKey)
                        }
                    )
                }

                entry<ActivityKey> {
                    ActivityScreen(
                        viewModel = activityViewModel,
                        onTransactionClick = { id ->
                            transactionEntryViewModel.loadTransaction(id)
                            backStack.add(AddTransactionKey(id))
                        },
                        onAddNewTransaction = {
                            transactionEntryViewModel.setType(TransactionType.EXPENSE)
                            backStack.add(AddTransactionKey(null))
                        }
                    )
                }

                entry<InsightsKey> {
                    InsightsScreen(viewModel = insightsViewModel)
                }

                entry<BudgetsKey> {
                    BudgetsScreen(viewModel = budgetsViewModel)
                }

                entry<AddTransactionKey> { key ->
                    AddTransactionScreen(
                        viewModel = transactionEntryViewModel,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                entry<CategoryManagerKey> {
                    CategoryManagerScreen(
                        viewModel = categoryManagerViewModel,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                entry<SettingsKey> {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateToCategoryManager = {
                            backStack.add(CategoryManagerKey)
                        },
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                entry<OnboardingKey> {
                    OnboardingScreen(
                        viewModel = onboardingViewModel,
                        onFinish = {
                            backStack.clear()
                            backStack.add(HomeKey)
                        }
                    )
                }
            }
        )

        // Floating Glass Navigation Bar shown only on top-level destinations
        if (isTopLevelDestination) {
            GlassNavigationBar(
                currentDestination = currentNavDestination,
                onNavigateToDestination = { dest ->
                    val targetKey = when (dest) {
                        NavDestination.Home -> HomeKey
                        NavDestination.Activity -> ActivityKey
                        NavDestination.Insights -> InsightsKey
                        NavDestination.Budgets -> BudgetsKey
                    }
                    if (currentKey != targetKey) {
                        if (targetKey is HomeKey) {
                            backStack.clear()
                            backStack.add(HomeKey)
                        } else {
                            backStack.clear()
                            backStack.add(HomeKey)
                            backStack.add(targetKey)
                        }
                    }
                },
                onAddClick = {
                    transactionEntryViewModel.setType(TransactionType.EXPENSE)
                    backStack.add(AddTransactionKey(null))
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
}
