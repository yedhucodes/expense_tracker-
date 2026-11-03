package com.expensetracker.app.feature.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.components.CategoryIconTile
import com.expensetracker.app.core.design.components.GlassSegmentedControl
import com.expensetracker.app.core.design.components.GlassTextField
import com.expensetracker.app.core.design.components.PrimaryActionButton
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.TileSize
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.GlassCard
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.money.MoneyFormatter
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.domain.model.TransactionType

private data class PaymentMethodOption(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionEntryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val isDark = AppTheme.colors.isDark

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TransactionEntryEvent.SaveSuccess -> onNavigateBack()
                else -> Unit
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.occurredAt)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.setDate(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = AppTheme.colors.accentPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = AppTheme.colors.textSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val expenseColor = AppTheme.colors.expense
    val incomeColor = AppTheme.colors.income
    val typeAccentColor = if (state.type == TransactionType.EXPENSE) {
        expenseColor
    } else {
        incomeColor
    }

    val currencySymbol = remember(state.currencyCode) {
        MoneyFormatter.getSymbol(state.currencyCode)
    }

    val paymentMethods = remember {
        listOf(
            PaymentMethodOption("Card", "Card", Icons.Rounded.CreditCard),
            PaymentMethodOption("Cash", "Cash", Icons.Rounded.Payments),
            PaymentMethodOption("Bank", "Bank", Icons.Rounded.AccountBalance),
            PaymentMethodOption("Other", "Other", Icons.Rounded.MoreHoriz)
        )
    }

    AtmosphericBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            ScreenHeader(
                title = if (state.isEditing) "Edit Transaction" else "Add Transaction",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (state.isEditing) {
                        IconButton(
                            onClick = { viewModel.deleteCurrent(onNavigateBack) },
                            modifier = Modifier.size(AppSpacing.minTouchTarget)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete transaction",
                                tint = AppTheme.colors.expense
                            )
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppSpacing.lg)
            ) {
                // Reactive Liquid Glass Segmented Control: Expense vs Income
                GlassSegmentedControl(
                    items = listOf(TransactionType.EXPENSE, TransactionType.INCOME),
                    selectedIndex = if (state.type == TransactionType.EXPENSE) 0 else 1,
                    onItemSelected = { index ->
                        viewModel.setType(if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME)
                    },
                    itemLabel = { if (it == TransactionType.EXPENSE) "Expense" else "Income" },
                    activeColor = { if (it == TransactionType.EXPENSE) expenseColor else incomeColor },
                    height = 50.dp
                )

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Hero Amount Glass Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 22.dp, horizontal = AppSpacing.lg)
                    ) {
                        Text(
                            text = if (state.type == TransactionType.EXPENSE) "AMOUNT SPENT" else "AMOUNT RECEIVED",
                            style = AppTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                            color = AppTheme.colors.textSecondary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        // Large Hero Amount Row with Currency Symbol and Interactive Text Field
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currencySymbol,
                                style = TextStyle(
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = typeAccentColor
                                ),
                                modifier = Modifier.padding(end = 4.dp)
                            )

                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (state.rawAmount.isEmpty()) {
                                    Text(
                                        text = "0.00",
                                        style = TextStyle(
                                            fontSize = 44.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0x35FFFFFF) else Color(0x35000000)
                                        )
                                    )
                                }

                                BasicTextField(
                                    value = state.rawAmount,
                                    onValueChange = viewModel::onAmountChanged,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    textStyle = TextStyle(
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    ),
                                    cursorBrush = SolidColor(typeAccentColor)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = state.validationError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            state.validationError?.let { err ->
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                Text(
                                    text = err,
                                    style = AppTheme.typography.bodySmall,
                                    color = AppTheme.colors.expense,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Category Selection Header
                Text(
                    text = "Category",
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                // Category Grid with multi-line wrap and glowing liquid glass tiles
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.availableCategories.forEach { category ->
                        val isSelected = category.id == state.selectedCategoryId
                        val catStyle = CategoryRegistry.getCategory(category.id)

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                            label = "cat_scale_${category.id}"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(74.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(AppShapes.medium)
                                .clickable(role = Role.RadioButton) { viewModel.selectCategory(category.id) }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            val tileBg = if (isSelected) {
                                Brush.radialGradient(
                                    listOf(
                                        catStyle.color.copy(alpha = 0.40f),
                                        catStyle.color.copy(alpha = 0.16f)
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        if (isDark) Color(0x351E293B) else Color(0x60F1F5F9),
                                        if (isDark) Color(0x181E293B) else Color(0x30E2E8F0)
                                    )
                                )
                            }

                            val tileBorder = if (isSelected) {
                                Brush.verticalGradient(
                                    listOf(catStyle.color, catStyle.color.copy(alpha = 0.5f))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        if (isDark) Color(0x30FFFFFF) else Color(0x60000000),
                                        if (isDark) Color(0x0CFFFFFF) else Color(0x10000000)
                                    )
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(tileBg, RoundedCornerShape(16.dp))
                                    .border(if (isSelected) 2.dp else 1.dp, tileBorder, RoundedCornerShape(16.dp))
                            ) {
                                CategoryIconTile(
                                    iconKey = category.iconKey,
                                    color = if (isSelected) catStyle.color else if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                    size = TileSize.Standard
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = category.name,
                                style = AppTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 13.sp
                                ),
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary,
                                maxLines = 2,
                                minLines = 2,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Date Picker Glass Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(AppShapes.medium)
                        .clickable { showDatePicker = true },
                    shape = AppShapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppSpacing.md, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AppTheme.colors.accentPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, AppTheme.colors.accentPrimary.copy(alpha = 0.30f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarToday,
                                    contentDescription = null,
                                    tint = AppTheme.colors.accentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(AppSpacing.md))

                            Text(
                                text = "Date",
                                style = AppTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = AppTheme.colors.textPrimary
                            )
                        }

                        Text(
                            text = DatePeriodUtil.formatFullDate(state.occurredAt),
                            style = AppTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppTheme.colors.accentPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))

                // Merchant / Payee Liquid Glass Field
                GlassTextField(
                    value = state.merchant,
                    onValueChange = viewModel::setMerchant,
                    label = "Merchant / Payee (Optional)",
                    placeholder = "e.g. Starbucks, Amazon, Landlord",
                    leadingIcon = Icons.Rounded.Storefront
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                // Note / Description Liquid Glass Field
                GlassTextField(
                    value = state.note,
                    onValueChange = viewModel::setNote,
                    label = "Note (Optional)",
                    placeholder = "e.g. Lunch with team, monthly utility bill",
                    leadingIcon = Icons.Rounded.Description
                )

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Payment Method Selector
                Text(
                    text = "Payment Method",
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentMethods.forEach { method ->
                        val isSelected = state.paymentMethod == method.id
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.94f else 1f,
                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                            label = "pay_scale_${method.id}"
                        )

                        val pillBg = if (isSelected) {
                            Brush.verticalGradient(
                                listOf(
                                    AppTheme.colors.accentPrimary.copy(alpha = 0.32f),
                                    AppTheme.colors.accentPrimary.copy(alpha = 0.16f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    if (isDark) Color(0x351E293B) else Color(0x60F1F5F9),
                                    if (isDark) Color(0x181E293B) else Color(0x30E2E8F0)
                                )
                            )
                        }

                        val pillBorder = if (isSelected) {
                            Brush.verticalGradient(
                                listOf(AppTheme.colors.accentPrimary, AppTheme.colors.accentPrimary.copy(alpha = 0.45f))
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    if (isDark) Color(0x30FFFFFF) else Color(0x60000000),
                                    if (isDark) Color(0x0CFFFFFF) else Color(0x10000000)
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(PillShape)
                                .background(pillBg, PillShape)
                                .border(if (isSelected) 1.5.dp else 1.dp, pillBorder, PillShape)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    role = Role.RadioButton
                                ) {
                                    viewModel.setPaymentMethod(method.id)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = method.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) AppTheme.colors.accentPrimary else AppTheme.colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = method.label,
                                    style = AppTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (isDark) Color.White else AppTheme.colors.accentPrimary
                                    } else {
                                        AppTheme.colors.textSecondary
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xxl))
            }

            // Bottom Fixed Save Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
            ) {
                PrimaryActionButton(
                    text = if (state.isEditing) "Update Transaction" else "Save Transaction",
                    onClick = viewModel::save,
                    isLoading = state.isSaving,
                    enabled = state.amountMinor > 0,
                    backgroundColor = typeAccentColor
                )
            }
        }
    }
}
