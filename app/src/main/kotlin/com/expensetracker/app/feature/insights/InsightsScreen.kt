package com.expensetracker.app.feature.insights

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.expensetracker.app.core.design.components.GlassSegmentedControl
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.components.TileSize
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
import com.expensetracker.app.domain.model.CategorySpending
import dev.chrisbanes.haze.hazeSource

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val hazeState = LocalHazeState.current

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
                    title = "Insights",
                    subtitle = DatePeriodUtil.formatMonthYear(state.year, state.month)
                )

                val accentColor = AppTheme.colors.accentPrimary

                // Period Segmented Control with Reactive Liquid Glass
                GlassSegmentedControl(
                    items = InsightsPeriod.entries,
                    selectedIndex = InsightsPeriod.entries.indexOf(state.period),
                    onItemSelected = { viewModel.selectPeriod(InsightsPeriod.entries[it]) },
                    itemLabel = { it.label },
                    activeColor = { accentColor },
                    modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs),
                    height = 48.dp
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colors.accentPrimary)
                    }
                } else if (state.summary.totalSpentMinor == 0L && state.summary.totalIncomeMinor == 0L) {
                    EmptyState(
                        icon = Icons.Rounded.PieChart,
                        title = "No Expense Data",
                        description = "There are no recorded transactions for ${state.period.label.lowercase()}. Add expenses to visualize spending trends.",
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
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        // Metric Cards Grid (Total Spent, Daily Avg)
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                            ) {
                                // Total Spent Card
                                GlassCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(AppSpacing.md)
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Spent",
                                            style = AppTheme.typography.labelSmall,
                                            color = AppTheme.colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        AmountText(
                                            amountMinor = state.summary.totalSpentMinor,
                                            currencyCode = state.currencyCode,
                                            isIncome = false,
                                            includePrefix = false,
                                            displayStyle = AmountDisplayStyle.TitleCard
                                        )
                                    }
                                }

                                // Daily Average Card
                                GlassCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(AppSpacing.md)
                                ) {
                                    Column {
                                        Text(
                                            text = "Daily Average",
                                            style = AppTheme.typography.labelSmall,
                                            color = AppTheme.colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        AmountText(
                                            amountMinor = state.dailyAverageMinor,
                                            currencyCode = state.currencyCode,
                                            isIncome = false,
                                            includePrefix = false,
                                            displayStyle = AmountDisplayStyle.TitleCard
                                        )
                                    }
                                }
                            }
                        }

                        // Top Spending Category Highlight Glass Card
                        state.topCategory?.let { top ->
                            item {
                                val catStyle = CategoryRegistry.getCategory(top.categoryId)
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppSpacing.md)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CategoryIconTile(
                                            iconKey = top.iconKey,
                                            color = catStyle.color,
                                            size = TileSize.Standard
                                        )

                                        Spacer(modifier = Modifier.width(AppSpacing.md))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Top Expense Category",
                                                style = AppTheme.typography.labelSmall,
                                                color = AppTheme.colors.textSecondary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = top.categoryName,
                                                style = AppTheme.typography.titleMedium,
                                                color = AppTheme.colors.textPrimary
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            AmountText(
                                                amountMinor = top.spentMinor,
                                                currencyCode = state.currencyCode,
                                                isIncome = false,
                                                includePrefix = false,
                                                displayStyle = AmountDisplayStyle.BodyStandard
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${String.format("%.1f", top.percentageOfTotal)}% of total",
                                                style = AppTheme.typography.labelSmall,
                                                color = AppTheme.colors.accentPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Spectrum Bar Section
                        if (state.categoryBreakdown.isNotEmpty()) {
                            item {
                                Column {
                                    SectionHeader(
                                        title = "Category Breakdown",
                                        subtitle = "${state.categoryBreakdown.size} categories"
                                    )

                                    Spacer(modifier = Modifier.height(AppSpacing.xs))

                                    SpectrumBar(
                                        categories = state.categoryBreakdown,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(16.dp)
                                            .clip(PillShape)
                                    )
                                }
                            }
                        }

                        // Category Breakdown List
                        items(
                            items = state.categoryBreakdown,
                            key = { it.categoryId }
                        ) { category ->
                            CategorySpendingItem(
                                item = category,
                                currencyCode = state.currencyCode
                            )
                        }

                        // Cashflow Comparison Glass Card
                        item {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(AppSpacing.md)
                            ) {
                                Column {
                                    Text(
                                        text = "Monthly Net Flow",
                                        style = AppTheme.typography.labelMedium,
                                        color = AppTheme.colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AmountText(
                                        amountMinor = state.summary.netCashFlowMinor,
                                        currencyCode = state.currencyCode,
                                        isIncome = state.summary.netCashFlowMinor >= 0,
                                        includePrefix = true,
                                        displayStyle = AmountDisplayStyle.TitleCard
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Earned: ${MoneyFormatter.format(state.summary.totalIncomeMinor, state.currencyCode)}",
                                            style = AppTheme.typography.bodySmall,
                                            color = AppTheme.colors.income
                                        )
                                        Text(
                                            text = "Spent: ${MoneyFormatter.format(state.summary.totalSpentMinor, state.currencyCode)}",
                                            style = AppTheme.typography.bodySmall,
                                            color = AppTheme.colors.expense
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpectrumBar(
    categories: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        var currentX = 0f
        val w = size.width
        val h = size.height

        categories.forEach { cat ->
            val sliceWidth = (cat.percentageOfTotal / 100f) * w
            if (sliceWidth > 0f) {
                val catStyle = CategoryRegistry.getCategory(cat.categoryId)
                drawRect(
                    color = catStyle.color,
                    topLeft = Offset(currentX, 0f),
                    size = Size(sliceWidth, h)
                )
                currentX += sliceWidth
            }
        }
    }
}

@Composable
private fun CategorySpendingItem(
    item: CategorySpending,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    val catStyle = CategoryRegistry.getCategory(item.categoryId)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        shape = AppShapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconTile(
                iconKey = item.iconKey,
                color = catStyle.color,
                size = TileSize.Standard
            )

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.categoryName,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.transactionCount} transactions",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.sm))

            Column(horizontalAlignment = Alignment.End) {
                AmountText(
                    amountMinor = item.spentMinor,
                    currencyCode = currencyCode,
                    isIncome = false,
                    includePrefix = false,
                    displayStyle = AmountDisplayStyle.BodyStandard
                )

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(catStyle.color.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", item.percentageOfTotal)}%",
                        style = AppTheme.typography.labelSmall,
                        color = catStyle.color
                    )
                }
            }
        }
    }
}
