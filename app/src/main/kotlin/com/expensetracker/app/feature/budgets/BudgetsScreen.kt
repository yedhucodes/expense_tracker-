package com.expensetracker.app.feature.budgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.components.AmountDisplayStyle
import com.expensetracker.app.core.design.components.AmountText
import com.expensetracker.app.core.design.components.CategoryIconTile
import com.expensetracker.app.core.design.components.EmptyState
import com.expensetracker.app.core.design.components.GlassDialog
import com.expensetracker.app.core.design.components.GlassSegmentedControl
import com.expensetracker.app.core.design.components.GlassTextField
import com.expensetracker.app.core.design.components.PrimaryActionButton
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.SecondaryActionButton
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.components.TileSize
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.GlassCard
import com.expensetracker.app.core.design.theme.LocalHazeState
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.shuDingLiquidGlass
import com.expensetracker.app.core.money.MoneyFormatter
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.domain.model.BudgetThresholdState
import com.expensetracker.app.domain.model.BudgetWithProgress
import com.expensetracker.app.domain.model.Category
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    viewModel: BudgetsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val hazeState = LocalHazeState.current

    if (state.dialogState.isOpen) {
        BudgetDialog(
            dialogState = state.dialogState,
            currencyCode = state.currencyCode,
            availableCategories = state.availableCategories,
            onAmountChange = viewModel::onDialogAmountChanged,
            onCategorySelected = viewModel::setDialogCategoryId,
            onDismiss = viewModel::dismissDialog,
            onSave = viewModel::saveBudget,
            onDelete = { state.dialogState.budgetId?.let { viewModel.deleteBudget(it) } }
        )
    }

    AtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ScreenHeader(
                    title = "Budgets",
                    subtitle = DatePeriodUtil.formatMonthYear(state.year, state.month)
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colors.accentPrimary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = AppSpacing.lg,
                            end = AppSpacing.lg,
                            bottom = 124.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        // Overall Budget Card
                        item {
                            if (state.overallBudget != null) {
                                OverallBudgetCard(
                                    budget = state.overallBudget!!,
                                    currencyCode = state.currencyCode,
                                    onEdit = { viewModel.openEditDialog(state.overallBudget!!) }
                                )
                            } else {
                                SetOverallBudgetPrompt(
                                    onSetBudget = { viewModel.openCreateDialog(categoryId = null) }
                                )
                            }
                        }

                        // Category Budgets Header
                        item {
                            SectionHeader(
                                title = "Category Budgets",
                                subtitle = "${state.categoryBudgets.size} active",
                                actionLabel = "Add Budget",
                                onActionClick = { viewModel.openCreateDialog(categoryId = null) }
                            )
                        }

                        // Category Budgets List
                        if (state.categoryBudgets.isEmpty()) {
                            item {
                                EmptyState(
                                    icon = Icons.Rounded.AccountBalanceWallet,
                                    title = "No Category Budgets",
                                    description = "Set budgets for specific categories like Groceries, Dining, or Transport to control your discretionary spend.",
                                    actionLabel = "Add Category Budget",
                                    onActionClick = { viewModel.openCreateDialog(categoryId = null) },
                                    useSecondaryAction = state.overallBudget == null,
                                    modifier = Modifier.padding(vertical = AppSpacing.lg)
                                )
                            }
                        } else {
                            items(
                                items = state.categoryBudgets,
                                key = { it.budget.id }
                            ) { budgetProgress ->
                                CategoryBudgetCard(
                                    budget = budgetProgress,
                                    currencyCode = state.currencyCode,
                                    onEdit = { viewModel.openEditDialog(budgetProgress) }
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
private fun OverallBudgetCard(
    budget: BudgetWithProgress,
    currencyCode: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when (budget.thresholdState) {
        BudgetThresholdState.NORMAL -> AppTheme.colors.income
        BudgetThresholdState.WARNING_75 -> AppTheme.colors.warning
        BudgetThresholdState.DANGER_90 -> AppTheme.colors.expense.copy(alpha = 0.85f)
        BudgetThresholdState.OVER_100 -> AppTheme.colors.expense
    }

    val statusLabel = when (budget.thresholdState) {
        BudgetThresholdState.NORMAL -> "On Track • ${(budget.progressPercent * 100).toInt()}%"
        BudgetThresholdState.WARNING_75 -> "Caution • 75% reached"
        BudgetThresholdState.DANGER_90 -> "Warning • 90% reached"
        BudgetThresholdState.OVER_100 -> "Over Budget"
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.large
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Monthly Budget",
                        style = AppTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(progressColor.copy(alpha = 0.18f))
                            .border(1.dp, progressColor.copy(alpha = 0.35f), PillShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            style = AppTheme.typography.labelSmall,
                            color = progressColor
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit overall budget",
                        tint = AppTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Spent",
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    AmountText(
                        amountMinor = budget.spentMinor,
                        currencyCode = currencyCode,
                        isIncome = false,
                        includePrefix = false,
                        displayStyle = AmountDisplayStyle.TitleCard
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (budget.isOverBudget) "Over Limit" else "Remaining",
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    AmountText(
                        amountMinor = if (budget.isOverBudget) budget.spentMinor - budget.budget.amountMinor else budget.remainingMinor,
                        currencyCode = currencyCode,
                        isIncome = !budget.isOverBudget,
                        includePrefix = false,
                        displayStyle = AmountDisplayStyle.TitleCard
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            LinearProgressIndicator(
                progress = { (budget.progressPercent).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(PillShape),
                color = progressColor,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            Text(
                text = "Budget Limit: ${MoneyFormatter.format(budget.budget.amountMinor, currencyCode)}",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun SetOverallBudgetPrompt(
    onSetBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = AppTheme.colors.isDark
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shuDingLiquidGlass(
                shape = AppShapes.large,
                isDark = isDark,
                accentGlow = AppTheme.colors.accentPrimary,
                borderWidth = 1.dp
            )
            .padding(AppSpacing.xl)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.accentPrimary.copy(alpha = 0.18f))
                    .border(1.dp, AppTheme.colors.accentPrimary.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Savings,
                    contentDescription = null,
                    tint = AppTheme.colors.accentPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            Text(
                text = "Overall Monthly Budget",
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Set a monthly spending limit to receive health indicators and keep your spending on track.",
                style = AppTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = AppSpacing.sm)
            )
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            PrimaryActionButton(
                text = "Set Monthly Limit",
                leadingIcon = Icons.Rounded.Add,
                onClick = onSetBudget,
                modifier = Modifier.fillMaxWidth(0.65f)
            )
        }
    }
}

@Composable
private fun CategoryBudgetCard(
    budget: BudgetWithProgress,
    currencyCode: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryId = budget.budget.categoryId ?: ""
    val catStyle = CategoryRegistry.getCategory(categoryId)

    val progressColor = when (budget.thresholdState) {
        BudgetThresholdState.NORMAL -> AppTheme.colors.income
        BudgetThresholdState.WARNING_75 -> AppTheme.colors.warning
        BudgetThresholdState.DANGER_90 -> AppTheme.colors.expense.copy(alpha = 0.85f)
        BudgetThresholdState.OVER_100 -> AppTheme.colors.expense
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.medium,
        onClick = onEdit
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryIconTile(
                    iconKey = catStyle.iconKey,
                    color = catStyle.color,
                    size = TileSize.Standard
                )

                Spacer(modifier = Modifier.width(AppSpacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = catStyle.name,
                        style = AppTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (budget.isOverBudget) {
                            "Over by ${MoneyFormatter.format(budget.spentMinor - budget.budget.amountMinor, currencyCode)}"
                        } else {
                            "${MoneyFormatter.format(budget.remainingMinor, currencyCode)} remaining"
                        },
                        style = AppTheme.typography.bodySmall,
                        color = progressColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    AmountText(
                        amountMinor = budget.spentMinor,
                        currencyCode = currencyCode,
                        isIncome = false,
                        includePrefix = false,
                        displayStyle = AmountDisplayStyle.BodyStandard
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "of ${MoneyFormatter.format(budget.budget.amountMinor, currencyCode)}",
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            LinearProgressIndicator(
                progress = { (budget.progressPercent).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(PillShape),
                color = progressColor,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetDialog(
    dialogState: BudgetDialogState,
    currencyCode: String,
    availableCategories: List<Category>,
    onAmountChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    GlassDialog(
        onDismissRequest = onDismiss,
        title = if (dialogState.budgetId == null) "Set Budget" else "Edit Budget",
        dismissButton = {
            SecondaryActionButton(
                text = "Cancel",
                onClick = onDismiss
            )
        },
        confirmButton = {
            PrimaryActionButton(
                text = "Save",
                onClick = onSave,
                enabled = dialogState.amountMinor > 0
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            // Liquid Glass Scope selector: Overall vs Category
            GlassSegmentedControl(
                items = listOf("Overall", "Category"),
                selectedIndex = if (dialogState.selectedCategoryId == null) 0 else 1,
                onItemSelected = { index ->
                    if (index == 0) {
                        onCategorySelected(null)
                    } else {
                        if (dialogState.selectedCategoryId == null) {
                            onCategorySelected(availableCategories.firstOrNull()?.id ?: "cat_food")
                        }
                    }
                },
                itemLabel = { it },
                height = 42.dp
            )

            // If Category scope, pick category
            if (dialogState.selectedCategoryId != null) {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    val selectedName = availableCategories.firstOrNull { it.id == dialogState.selectedCategoryId }?.name ?: "Select Category"
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.accentPrimary,
                            unfocusedBorderColor = AppTheme.colors.divider
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategorySelected(category.id)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Limit Amount Field with GlassTextField
            GlassTextField(
                value = dialogState.rawAmount,
                onValueChange = onAmountChange,
                label = "Monthly Limit ($currencyCode)",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = dialogState.validationError != null,
                errorMessage = dialogState.validationError
            )

            if (dialogState.budgetId != null) {
                SecondaryActionButton(
                    text = "Delete Budget",
                    onClick = onDelete
                )
            }
        }
    }
}

