package com.expensetracker.app.feature.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.core.design.components.GlassDialog
import com.expensetracker.app.core.design.components.PrimaryActionButton
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.SecondaryActionButton
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.GlassCard
import com.expensetracker.app.core.design.theme.LocalHazeState
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.liquidGlass
import com.expensetracker.app.core.design.theme.shuDingLiquidGlass
import dev.chrisbanes.haze.hazeSource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToCategoryManager: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val hazeState = LocalHazeState.current

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShareCsvFile -> {
                    context.startActivity(event.fileIntent)
                }
                is SettingsEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showCurrencyDialog) {
        GlassDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = "Select Currency",
            confirmButton = {
                PrimaryActionButton(
                    text = "Close",
                    onClick = { showCurrencyDialog = false }
                )
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                viewModel.availableCurrencies.forEach { (code, label) ->
                    val isSelected = state.currencyCode == code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(PillShape)
                            .background(if (isSelected) AppTheme.colors.accentPrimary.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                viewModel.setCurrency(code)
                                showCurrencyDialog = false
                            }
                            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                viewModel.setCurrency(code)
                                showCurrencyDialog = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppTheme.colors.accentPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text(
                            text = label,
                            style = AppTheme.typography.bodyLarge,
                            color = if (isSelected) AppTheme.colors.accentPrimary else AppTheme.colors.textPrimary
                        )
                    }
                }
            }
        }
    }

    if (showThemeDialog) {
        val themes = listOf(
            "system" to "System Default",
            "dark" to "Dark Mode",
            "light" to "Light Mode"
        )
        GlassDialog(
            onDismissRequest = { showThemeDialog = false },
            title = "App Theme",
            confirmButton = {
                PrimaryActionButton(
                    text = "Close",
                    onClick = { showThemeDialog = false }
                )
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                themes.forEach { (mode, label) ->
                    val isSelected = state.themeMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(PillShape)
                            .background(if (isSelected) AppTheme.colors.accentPrimary.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                viewModel.setTheme(mode)
                                showThemeDialog = false
                            }
                            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                viewModel.setTheme(mode)
                                showThemeDialog = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppTheme.colors.accentPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text(
                            text = label,
                            style = AppTheme.typography.bodyLarge,
                            color = if (isSelected) AppTheme.colors.accentPrimary else AppTheme.colors.textPrimary
                        )
                    }
                }
            }
        }
    }

    if (state.isClearDataDialogOpen) {
        GlassDialog(
            onDismissRequest = viewModel::dismissClearDataDialog,
            title = "Clear All Data?",
            dismissButton = {
                SecondaryActionButton(
                    text = "Cancel",
                    onClick = viewModel::dismissClearDataDialog
                )
            },
            confirmButton = {
                PrimaryActionButton(
                    text = "Delete Everything",
                    onClick = viewModel::confirmClearAllData,
                    backgroundColor = AppTheme.colors.expense
                )
            }
        ) {
            Text(
                text = "This action permanently deletes all recorded transactions and budget configurations from your device. This cannot be undone.",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textSecondary
            )
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
                    title = "Settings",
                    onNavigateBack = onNavigateBack
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppSpacing.lg,
                        end = AppSpacing.lg,
                        top = AppSpacing.xs,
                        bottom = 48.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    // Section: Preferences
                    item {
                        SectionHeader(title = "Preferences")
                    }

                    item {
                        SettingsItem(
                            icon = Icons.Rounded.Paid,
                            title = "Currency",
                            subtitle = viewModel.availableCurrencies.firstOrNull { it.first == state.currencyCode }?.second ?: state.currencyCode,
                            onClick = { showCurrencyDialog = true }
                        )
                    }

                    item {
                        val themeLabel = when (state.themeMode) {
                            "dark" -> "Dark Mode"
                            "light" -> "Light Mode"
                            else -> "System Default"
                        }
                        SettingsItem(
                            icon = Icons.Rounded.DarkMode,
                            title = "Appearance",
                            subtitle = themeLabel,
                            onClick = { showThemeDialog = true }
                        )
                    }

                    // Section: Customization
                    item {
                        SectionHeader(title = "Categories")
                    }

                    item {
                        SettingsItem(
                            icon = Icons.Rounded.Category,
                            title = "Manage Categories",
                            subtitle = "Customize expense & income categories, icons, and colors",
                            onClick = onNavigateToCategoryManager
                        )
                    }

                    // Section: Data & Backup
                    item {
                        SectionHeader(title = "Data Management")
                    }

                    item {
                        SettingsItem(
                            icon = Icons.Rounded.FileDownload,
                            title = "Export to CSV",
                            subtitle = if (state.isExporting) "Generating export..." else "Export all recorded transactions to a spreadsheet",
                            onClick = { viewModel.exportCsv(context) },
                            trailing = {
                                if (state.isExporting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = AppTheme.colors.accentPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = null,
                                        tint = AppTheme.colors.textTertiary
                                    )
                                }
                            }
                        )
                    }

                    item {
                        SettingsItem(
                            icon = Icons.Rounded.DeleteForever,
                            title = "Clear All Data",
                            subtitle = "Delete all transactions and custom budgets",
                            titleColor = AppTheme.colors.expense,
                            iconTint = AppTheme.colors.expense,
                            onClick = viewModel::openClearDataDialog
                        )
                    }

                    // Section: About
                    item {
                        SectionHeader(title = "About")
                    }

                    item {
                        val isDark = AppTheme.colors.isDark
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shuDingLiquidGlass(
                                    shape = AppShapes.large,
                                    isDark = isDark,
                                    accentGlow = AppTheme.colors.accentPrimary,
                                    borderWidth = 1.dp
                                )
                                .padding(AppSpacing.lg)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AppTheme.colors.accentPrimary.copy(alpha = 0.18f))
                                            .border(1.dp, AppTheme.colors.accentPrimary.copy(alpha = 0.35f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Info,
                                            contentDescription = null,
                                            tint = AppTheme.colors.accentPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(AppSpacing.md))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "[APP_NAME]",
                                            style = AppTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(PillShape)
                                            .background(if (isDark) Color(0x25FFFFFF) else Color(0x12000000))
                                            .border(1.dp, if (isDark) Color(0x35FFFFFF) else Color(0x18000000), PillShape)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "v1.0.0",
                                            style = AppTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(AppSpacing.md))

                                Text(
                                    text = "An offline-first, privacy-respecting expense tracker engineered with strict minor-unit monetary precision. All your financial records remain exclusively on your device.",
                                    style = AppTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                                    color = AppTheme.colors.textSecondary
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
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: androidx.compose.ui.graphics.Color = AppTheme.colors.textPrimary,
    iconTint: androidx.compose.ui.graphics.Color = AppTheme.colors.accentPrimary,
    trailing: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = AppTheme.colors.textTertiary
        )
    }
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = 0.65f,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "settings_press_scale"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = AppShapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(AppShapes.small)
                    .background(iconTint.copy(alpha = 0.15f))
                    .border(1.dp, iconTint.copy(alpha = 0.30f), AppShapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.sm))

            trailing()
        }
    }
}

