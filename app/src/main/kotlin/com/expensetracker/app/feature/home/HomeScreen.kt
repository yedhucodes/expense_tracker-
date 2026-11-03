package com.expensetracker.app.feature.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.components.AmountDisplayStyle
import com.expensetracker.app.core.design.components.AmountText
import com.expensetracker.app.core.design.components.CategoryIconTile
import com.expensetracker.app.core.design.components.EmptyState
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.components.TileSize
import com.expensetracker.app.core.design.components.TransactionRow
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.GlassCard
import com.expensetracker.app.core.design.theme.LocalHazeState
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.liquidGlass
import com.expensetracker.app.core.money.MoneyFormatter
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.domain.model.BudgetThresholdState
import com.expensetracker.app.domain.model.BudgetWithProgress
import com.expensetracker.app.domain.model.CategorySpending
import dev.chrisbanes.haze.hazeSource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactionDetail: (String) -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val hazeState = LocalHazeState.current

    AtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
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
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // Month Selector Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Overview",
                                    style = AppTheme.typography.titleLarge,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = DatePeriodUtil.formatMonthYear(state.year, state.month),
                                    style = AppTheme.typography.bodyMedium,
                                    color = AppTheme.colors.textSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = viewModel::previousMonth,
                                    modifier = Modifier.size(AppSpacing.minTouchTarget)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronLeft,
                                        contentDescription = "Previous month",
                                        tint = AppTheme.colors.textPrimary
                                    )
                                }

                                IconButton(
                                    onClick = viewModel::nextMonth,
                                    modifier = Modifier.size(AppSpacing.minTouchTarget)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "Next month",
                                        tint = AppTheme.colors.textPrimary
                                    )
                                }

                                IconButton(
                                    onClick = onNavigateToSettings,
                                    modifier = Modifier.size(AppSpacing.minTouchTarget)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Settings",
                                        tint = AppTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Financial Overview Card (Liquid Glass Hero)
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
                        ) {
                            Column(modifier = Modifier.padding(AppSpacing.lg)) {
                                Text(
                                    text = "Net Cash Flow",
                                    style = AppTheme.typography.labelMedium,
                                    color = AppTheme.colors.textSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                AmountText(
                                    amountMinor = state.summary.netCashFlowMinor,
                                    currencyCode = state.currencyCode,
                                    isIncome = state.summary.netCashFlowMinor >= 0,
                                    includePrefix = true,
                                    displayStyle = AmountDisplayStyle.DisplayHero
                                )

                                Spacer(modifier = Modifier.height(AppSpacing.lg))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(AppShapes.medium)
                                        .background(if (AppTheme.colors.isDark) Color(0x351F293D) else Color(0x40FFFFFF))
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.verticalGradient(
                                                listOf(
                                                    if (AppTheme.colors.isDark) Color(0x28FFFFFF) else Color(0x70FFFFFF),
                                                    if (AppTheme.colors.isDark) Color(0x0AFFFFFF) else Color(0x15000000)
                                                )
                                            ),
                                            shape = AppShapes.medium
                                        )
                                        .padding(AppSpacing.md),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Spent
                                    Column {
                                        Text(
                                            text = "Total Spent",
                                            style = AppTheme.typography.labelSmall,
                                            color = AppTheme.colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        AmountText(
                                            amountMinor = state.summary.totalSpentMinor,
                                            currencyCode = state.currencyCode,
                                            isIncome = false,
                                            includePrefix = false,
                                            displayStyle = AmountDisplayStyle.TitleCard
                                        )
                                    }

                                    // Income
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Total Income",
                                            style = AppTheme.typography.labelSmall,
                                            color = AppTheme.colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        AmountText(
                                            amountMinor = state.summary.totalIncomeMinor,
                                            currencyCode = state.currencyCode,
                                            isIncome = true,
                                            includePrefix = false,
                                            displayStyle = AmountDisplayStyle.TitleCard
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Overall Budget Card (if exists)
                    state.overallBudget?.let { budget ->
                        item {
                            BudgetGlanceCard(
                                budget = budget,
                                currencyCode = state.currencyCode,
                                onClick = onNavigateToBudgets,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
                            )
                        }
                    }

                    // Daily Spending Pulse Sparkline
                    if (state.summary.totalSpentMinor > 0 && state.dailySpendPoints.isNotEmpty()) {
                        item {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
                            ) {
                                Column(modifier = Modifier.padding(AppSpacing.md)) {
                                    Text(
                                        text = "Monthly Spending Pulse",
                                        style = AppTheme.typography.labelMedium,
                                        color = AppTheme.colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                                    DailySpendPulse(
                                        points = state.dailySpendPoints,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Top Spending Categories
                    if (state.topCategories.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Top Spending",
                                subtitle = "${state.topCategories.size} categories",
                                modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = AppSpacing.lg),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                            ) {
                                state.topCategories.forEach { cat ->
                                    TopCategoryRow(
                                        category = cat,
                                        currencyCode = state.currencyCode
                                    )
                                }
                            }
                        }
                    }

                    // Recent Transactions Header
                    item {
                        SectionHeader(
                            title = "Recent Activity",
                            actionLabel = if (state.recentTransactions.isNotEmpty()) "See All" else null,
                            onActionClick = onNavigateToActivity,
                            modifier = Modifier.padding(
                                start = AppSpacing.lg,
                                end = AppSpacing.lg,
                                top = AppSpacing.md,
                                bottom = AppSpacing.xs
                            )
                        )
                    }

                    // Recent Transactions List
                    if (state.recentTransactions.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                                title = "No Activity Yet",
                                description = "Record your first expense or income to see your dashboard update live.",
                                actionLabel = "Add Transaction",
                                onActionClick = onNavigateToAddTransaction,
                                modifier = Modifier.padding(vertical = AppSpacing.xl)
                            )
                        }
                    } else {
                        items(
                            items = state.recentTransactions,
                            key = { it.id }
                        ) { tx ->
                            Box(modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = 2.dp)) {
                                TransactionRow(
                                    transaction = tx,
                                    onClick = { onNavigateToTransactionDetail(tx.id) },
                                    showDate = true
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
private fun BudgetGlanceCard(
    budget: BudgetWithProgress,
    currencyCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when (budget.thresholdState) {
        BudgetThresholdState.NORMAL -> AppTheme.colors.income
        BudgetThresholdState.WARNING_75 -> AppTheme.colors.warning
        BudgetThresholdState.DANGER_90 -> AppTheme.colors.expense.copy(alpha = 0.85f)
        BudgetThresholdState.OVER_100 -> AppTheme.colors.expense
    }

    GlassCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Budget",
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary
                )

                Text(
                    text = if (budget.isOverBudget) {
                        "Over by ${MoneyFormatter.format(budget.spentMinor - budget.budget.amountMinor, currencyCode)}"
                    } else {
                        "${MoneyFormatter.format(budget.remainingMinor, currencyCode)} left"
                    },
                    style = AppTheme.typography.labelMedium,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            LinearProgressIndicator(
                progress = { (budget.progressPercent).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(PillShape),
                color = progressColor,
                trackColor = AppTheme.colors.surfaceRaised
            )
        }
    }
}

@Composable
private fun DailySpendPulse(
    points: List<DailySpendPoint>,
    modifier: Modifier = Modifier
) {
    val barColor = AppTheme.colors.accentPrimary
    val maxMinor = points.maxOfOrNull { it.amountMinor }?.coerceAtLeast(1L) ?: 1L

    Canvas(modifier = modifier) {
        val totalBars = points.size
        if (totalBars == 0) return@Canvas

        val barSpacing = 2.dp.toPx()
        val totalSpacing = barSpacing * (totalBars - 1)
        val barWidth = ((size.width - totalSpacing) / totalBars).coerceAtLeast(2.dp.toPx())

        points.forEachIndexed { index, point ->
            val ratio = (point.amountMinor.toFloat() / maxMinor.toFloat()).coerceIn(0.06f, 1f)
            val barHeight = size.height * ratio
            val x = index * (barWidth + barSpacing)
            val y = size.height - barHeight

            val color = if (point.amountMinor > 0) barColor else barColor.copy(alpha = 0.20f)

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun TopCategoryRow(
    category: CategorySpending,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    val catStyle = CategoryRegistry.getCategory(category.categoryId)

    GlassCard(
        modifier = modifier,
        shape = AppShapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconTile(
                iconKey = category.iconKey,
                color = catStyle.color,
                size = TileSize.Standard
            )

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.categoryName,
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${String.format("%.1f", category.percentageOfTotal)}% of total • ${category.transactionCount} txs",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.sm))

            AmountText(
                amountMinor = category.spentMinor,
                currencyCode = currencyCode,
                isIncome = false,
                includePrefix = false,
                displayStyle = AmountDisplayStyle.BodyStandard
            )
        }
    }
}
