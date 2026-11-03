package com.expensetracker.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.components.PrimaryActionButton
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.GlassCard
import com.expensetracker.app.core.design.theme.LocalHazeState
import com.expensetracker.app.core.design.theme.PillShape
import dev.chrisbanes.haze.hazeSource

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val hazeState = LocalHazeState.current

    AtmosphericBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(AppSpacing.xxl))

                // App Icon / Logo Placeholder
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(AppShapes.large)
                        .background(AppTheme.colors.accentPrimary.copy(alpha = 0.22f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), AppShapes.large),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Savings,
                        contentDescription = null,
                        tint = AppTheme.colors.accentPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                Text(
                    text = "Welcome to [APP_NAME]",
                    style = AppTheme.typography.headlineMedium,
                    color = AppTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                Text(
                    text = "Effortless, offline personal financial clarity.",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Privacy Guarantee Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.medium
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(AppShapes.small)
                                .background(AppTheme.colors.income.copy(alpha = 0.18f))
                                .border(1.dp, AppTheme.colors.income.copy(alpha = 0.35f), AppShapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = AppTheme.colors.income,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(AppSpacing.md))

                        Column {
                            Text(
                                text = "100% Offline & Private",
                                style = AppTheme.typography.titleMedium,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "No account required, zero tracking, and no external servers. Your transactions and budgets are stored strictly on this device.",
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Currency Selection
                Text(
                    text = "Choose Your Currency",
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    viewModel.availableCurrencies.forEach { (code, label) ->
                        val isSelected = state.selectedCurrency == code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(AppShapes.medium)
                                .background(
                                    if (isSelected) AppTheme.colors.accentPrimary.copy(alpha = 0.18f)
                                    else if (AppTheme.colors.isDark) Color(0x331E293B) else Color(0x33E2E8F0)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) AppTheme.colors.accentPrimary else Color.White.copy(alpha = 0.12f),
                                    shape = AppShapes.medium
                                )
                                .clickable(role = Role.RadioButton) { viewModel.selectCurrency(code) }
                                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = AppTheme.typography.bodyLarge,
                                color = if (isSelected) AppTheme.colors.accentPrimary else AppTheme.colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Text(
                                    text = "Selected",
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.colors.accentPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Optional Monthly Budget
                Text(
                    text = "Monthly Spending Goal (Optional)",
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                OutlinedTextField(
                    value = state.initialBudgetRaw,
                    onValueChange = viewModel::onBudgetChanged,
                    placeholder = { Text("e.g. 1500.00", color = AppTheme.colors.textSecondary) },
                    prefix = { Text("${state.selectedCurrency} ", color = AppTheme.colors.textSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTheme.colors.accentPrimary,
                        unfocusedBorderColor = AppTheme.colors.divider,
                        focusedTextColor = AppTheme.colors.textPrimary,
                        unfocusedTextColor = AppTheme.colors.textPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(AppSpacing.lg)
            ) {
                PrimaryActionButton(
                    text = "Get Started",
                    onClick = { viewModel.completeOnboarding(onFinish) },
                    isLoading = state.isCompleting
                )
            }
        }
    }
}

