package com.expensetracker.app.feature.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.core.design.components.AmountDisplayStyle
import com.expensetracker.app.core.design.components.AmountText
import com.expensetracker.app.core.design.components.EmptyState
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.components.TransactionRow
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.GlassCard
import com.expensetracker.app.core.design.theme.LocalHazeState
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.liquidGlass
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType
import dev.chrisbanes.haze.hazeSource

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    onTransactionClick: (String) -> Unit,
    onAddNewTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val hazeState = LocalHazeState.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ActivityEvent.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDelete(event.transaction)
                    }
                }
            }
        }
    }

    AtmosphericBackground(modifier = modifier) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ScreenHeader(
                    title = "Activity",
                    subtitle = "${state.filteredCount} transactions"
                )

                // Search Bar with liquid glass treatment
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        placeholder = {
                            Text(
                                "Search transactions, merchants, notes...",
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.textSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = AppTheme.colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "Clear search",
                                        tint = AppTheme.colors.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = AppShapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.accentPrimary,
                            unfocusedBorderColor = if (AppTheme.colors.isDark) Color(0x30FFFFFF) else Color(0x60000000),
                            focusedContainerColor = if (AppTheme.colors.isDark) Color(0x55151D2A) else Color(0x80FFFFFF),
                            unfocusedContainerColor = if (AppTheme.colors.isDark) Color(0x40151D2A) else Color(0x60FFFFFF),
                            focusedTextColor = AppTheme.colors.textPrimary,
                            unfocusedTextColor = AppTheme.colors.textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    // Type Filter: All
                    FilterChip(
                        text = "All Types",
                        isSelected = state.selectedType == null,
                        onClick = { viewModel.setTypeFilter(null) }
                    )

                    // Type Filter: Expenses
                    FilterChip(
                        text = "Expenses",
                        isSelected = state.selectedType == TransactionType.EXPENSE,
                        onClick = { viewModel.setTypeFilter(TransactionType.EXPENSE) }
                    )

                    // Type Filter: Income
                    FilterChip(
                        text = "Income",
                        isSelected = state.selectedType == TransactionType.INCOME,
                        onClick = { viewModel.setTypeFilter(TransactionType.INCOME) }
                    )

                    // Date Filters
                    ActivityDateFilter.entries.forEach { filter ->
                        FilterChip(
                            text = filter.label,
                            isSelected = state.selectedDateFilter == filter,
                            onClick = { viewModel.setDateFilter(filter) }
                        )
                    }

                    // Category Filter (if active, show clear chip)
                    if (state.selectedCategoryId != null) {
                        val catName = state.availableCategories.firstOrNull { it.id == state.selectedCategoryId }?.name ?: "Category"
                        FilterChip(
                            text = "$catName ✕",
                            isSelected = true,
                            onClick = { viewModel.setCategoryFilter(null) }
                        )
                    }

                    // Payment Method Filter (if active, show clear chip)
                    if (state.selectedPaymentMethod != null) {
                        FilterChip(
                            text = "${state.selectedPaymentMethod} ✕",
                            isSelected = true,
                            onClick = { viewModel.setPaymentMethodFilter(null) }
                        )
                    }
                }

                // Filter Summary Glass Banner
                if (state.filteredCount > 0) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
                            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                        shape = AppShapes.small
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Spent: ",
                                    style = AppTheme.typography.bodySmall,
                                    color = AppTheme.colors.textSecondary
                                )
                                AmountText(
                                    amountMinor = state.totalExpenseMinor,
                                    currencyCode = state.currencyCode,
                                    isIncome = false,
                                    includePrefix = true,
                                    displayStyle = AmountDisplayStyle.BodyStandard
                                )
                            }

                            if (state.totalIncomeMinor > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Income: ",
                                        style = AppTheme.typography.bodySmall,
                                        color = AppTheme.colors.textSecondary
                                    )
                                    AmountText(
                                        amountMinor = state.totalIncomeMinor,
                                        currencyCode = state.currencyCode,
                                        isIncome = true,
                                        includePrefix = true,
                                        displayStyle = AmountDisplayStyle.BodyStandard
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                // Content List
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colors.accentPrimary)
                    }
                } else if (state.groupedTransactions.isEmpty()) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                        animationRes = R.raw.animation_empty_activity,
                        title = "No Transactions Found",
                        description = if (state.searchQuery.isNotBlank() || state.selectedType != null || state.selectedCategoryId != null) {
                            "Try adjusting or clearing your filters to view more activity."
                        } else {
                            "You haven't recorded any transactions yet. Tap the button below to add your first one!"
                        },
                        actionLabel = if (state.searchQuery.isNotBlank() || state.selectedType != null) "Clear Filters" else "Add Transaction",
                        onActionClick = {
                            if (state.searchQuery.isNotBlank() || state.selectedType != null) {
                                viewModel.onSearchQueryChanged("")
                                viewModel.setTypeFilter(null)
                                viewModel.setCategoryFilter(null)
                                viewModel.setDateFilter(ActivityDateFilter.ALL)
                                viewModel.setPaymentMethodFilter(null)
                            } else {
                                onAddNewTransaction()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = AppSpacing.lg,
                            end = AppSpacing.lg,
                            bottom = 124.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        state.groupedTransactions.forEach { (sectionTitle, transactions) ->
                            item(key = "header_$sectionTitle") {
                                SectionHeader(
                                    title = sectionTitle,
                                    subtitle = "${transactions.size} items",
                                    modifier = Modifier.padding(top = AppSpacing.md, bottom = AppSpacing.xs)
                                )
                            }

                            items(
                                items = transactions,
                                key = { it.id }
                            ) { tx ->
                                TransactionRow(
                                    transaction = tx,
                                    onClick = { onTransactionClick(tx.id) },
                                    showDate = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .liquidGlass(
                shape = PillShape,
                tint = if (isSelected) AppTheme.colors.accentPrimary.copy(alpha = 0.28f) else if (AppTheme.colors.isDark) Color(0x40151D2A) else Color(0x60FFFFFF),
                blurRadius = 16.dp,
                borderTopColor = if (isSelected) AppTheme.colors.accentPrimary else if (AppTheme.colors.isDark) Color(0x35FFFFFF) else Color(0x80FFFFFF),
                borderBottomColor = if (isSelected) AppTheme.colors.accentPrimary.copy(alpha = 0.4f) else if (AppTheme.colors.isDark) Color(0x0AFFFFFF) else Color(0x15000000)
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = AppSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTheme.typography.labelMedium,
            color = if (isSelected) AppTheme.colors.accentPrimary else AppTheme.colors.textSecondary
        )
    }
}
