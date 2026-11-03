package com.expensetracker.app.feature.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.category.CategoryType
import com.expensetracker.app.core.design.category.CuratedPalette
import com.expensetracker.app.core.design.components.CategoryIconTile
import com.expensetracker.app.core.design.components.GlassDialog
import com.expensetracker.app.core.design.components.GlassSegmentedControl
import com.expensetracker.app.core.design.components.GlassTextField
import com.expensetracker.app.core.design.components.PrimaryActionButton
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.SecondaryActionButton
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.components.TileSize
import com.expensetracker.app.core.design.icons.IconRegistry
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.AtmosphericBackground
import com.expensetracker.app.core.design.theme.LocalHazeState
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.liquidGlass
import com.expensetracker.app.domain.model.Category
import dev.chrisbanes.haze.hazeSource

@Composable
fun CategoryManagerScreen(
    viewModel: CategoryManagerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val hazeState = LocalHazeState.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoryManagerEvent.ShowToast -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (state.createState.isOpen) {
        CreateCategoryDialog(
            createState = state.createState,
            onNameChange = viewModel::onNameChanged,
            onTypeChange = viewModel::onTypeChanged,
            onColorSelect = viewModel::onColorSelected,
            onIconSelect = viewModel::onIconSelected,
            onDismiss = viewModel::dismissCreateDialog,
            onSave = viewModel::saveCategory
        )
    }

    if (state.reassignState.isOpen) {
        ReassignDeleteDialog(
            reassignState = state.reassignState,
            availableTargets = if (state.reassignState.categoryToDelete?.type == CategoryType.INCOME) {
                state.incomeCategories.filter { it.id != state.reassignState.categoryToDelete?.id }
            } else {
                state.expenseCategories.filter { it.id != state.reassignState.categoryToDelete?.id }
            },
            onTargetSelected = viewModel::setReassignTarget,
            onDismiss = viewModel::dismissReassignDialog,
            onConfirm = viewModel::confirmReassignAndDelete
        )
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
                    title = "Categories",
                    onNavigateBack = onNavigateBack,
                    actions = {
                        IconButton(onClick = { viewModel.openCreateDialog() }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Create Category",
                                tint = AppTheme.colors.accentPrimary
                            )
                        }
                    }
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
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        // Expense Section
                        item {
                            SectionHeader(
                                title = "Expense Categories",
                                subtitle = "${state.expenseCategories.size} categories"
                            )
                        }

                        items(
                            items = state.expenseCategories,
                            key = { it.id }
                        ) { cat ->
                            CategoryRowItem(
                                category = cat,
                                onDelete = { viewModel.requestDeleteCategory(cat) }
                            )
                        }

                        // Income Section
                        item {
                            Spacer(modifier = Modifier.height(AppSpacing.md))
                            SectionHeader(
                                title = "Income Categories",
                                subtitle = "${state.incomeCategories.size} categories"
                            )
                        }

                        items(
                            items = state.incomeCategories,
                            key = { it.id }
                        ) { cat ->
                            CategoryRowItem(
                                category = cat,
                                onDelete = { viewModel.requestDeleteCategory(cat) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRowItem(
    category: Category,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val catStyle = CategoryRegistry.getCategory(category.id)
    val color = CategoryRegistry.parseHexColor(category.colorHex, catStyle.color)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = AppShapes.medium
            )
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIconTile(
            iconKey = category.iconKey,
            color = color,
            size = TileSize.Standard
        )

        Spacer(modifier = Modifier.width(AppSpacing.md))

        Text(
            text = category.name,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (category.isBuiltIn) {
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(AppTheme.colors.surfaceRaised)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Built-in",
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textTertiary
                )
            }
        } else {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(AppSpacing.minTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete ${category.name}",
                    tint = AppTheme.colors.expense
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateCategoryDialog(
    createState: CreateCategoryState,
    onNameChange: (String) -> Unit,
    onTypeChange: (CategoryType) -> Unit,
    onColorSelect: (String) -> Unit,
    onIconSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val currentColor = CategoryRegistry.parseHexColor(createState.colorHex, AppTheme.colors.accentPrimary)
    val isDark = AppTheme.colors.isDark
    val expenseColor = AppTheme.colors.expense
    val incomeColor = AppTheme.colors.income

    GlassDialog(
        onDismissRequest = onDismiss,
        title = "New Category",
        dismissButton = {
            SecondaryActionButton(
                text = "Cancel",
                onClick = onDismiss
            )
        },
        confirmButton = {
            PrimaryActionButton(
                text = "Create",
                onClick = onSave,
                enabled = createState.name.isNotBlank(),
                backgroundColor = currentColor
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            // Live Preview Liquid Glass Tile
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(currentColor.copy(alpha = 0.35f), currentColor.copy(alpha = 0.12f))
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(listOf(currentColor, currentColor.copy(alpha = 0.35f))),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CategoryIconTile(
                    iconKey = createState.iconKey,
                    color = currentColor,
                    size = TileSize.Large
                )
            }

            // Liquid Glass Type Segmented Control
            GlassSegmentedControl(
                items = listOf(CategoryType.EXPENSE, CategoryType.INCOME),
                selectedIndex = if (createState.type == CategoryType.EXPENSE) 0 else 1,
                onItemSelected = { index ->
                    onTypeChange(if (index == 0) CategoryType.EXPENSE else CategoryType.INCOME)
                },
                itemLabel = { if (it == CategoryType.EXPENSE) "Expense" else "Income" },
                activeColor = { if (it == CategoryType.EXPENSE) expenseColor else incomeColor },
                height = 42.dp
            )

            // Category Name Field
            GlassTextField(
                value = createState.name,
                onValueChange = onNameChange,
                label = "Category Name",
                placeholder = "e.g. Groceries, Freelance",
                isError = createState.validationError != null,
                errorMessage = createState.validationError
            )

            // Color Swatches
            Text(
                text = "Color",
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.align(Alignment.Start)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (hex in CuratedPalette) {
                    val swatchColor = CategoryRegistry.parseHexColor(hex, Color.Gray)
                    val isSelected = createState.colorHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatchColor)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color.White else Color(0x30FFFFFF),
                                shape = CircleShape
                            )
                            .clickable { onColorSelect(hex) }
                    )
                }
            }

            // Icon Grid
            Text(
                text = "Icon",
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.align(Alignment.Start)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (key in IconRegistry.AvailableIcons) {
                    val isSelected = createState.iconKey == key
                    val itemBg = if (isSelected) {
                        Brush.verticalGradient(
                            listOf(
                                currentColor.copy(alpha = 0.35f),
                                currentColor.copy(alpha = 0.15f)
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

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemBg, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) currentColor else Color(0x20FFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onIconSelect(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = IconRegistry.getIcon(key),
                            contentDescription = null,
                            tint = if (isSelected) currentColor else AppTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReassignDeleteDialog(
    reassignState: ReassignDeleteState,
    availableTargets: List<Category>,
    onTargetSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    GlassDialog(
        onDismissRequest = onDismiss,
        title = "Reassign Transactions",
        dismissButton = {
            SecondaryActionButton(
                text = "Cancel",
                onClick = onDismiss
            )
        },
        confirmButton = {
            PrimaryActionButton(
                text = "Reassign & Delete",
                onClick = onConfirm,
                enabled = reassignState.targetCategoryId != null,
                backgroundColor = AppTheme.colors.expense
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            Text(
                text = "There are ${reassignState.affectedTransactionCount} existing transactions in \"${reassignState.categoryToDelete?.name}\". Please choose a target category to reassign them to before deleting.",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textSecondary
            )

            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                val targetName = availableTargets.firstOrNull { it.id == reassignState.targetCategoryId }?.name ?: "Select Target"
                OutlinedTextField(
                    value = targetName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reassign To") },
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
                    availableTargets.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                onTargetSelected(cat.id)
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}

