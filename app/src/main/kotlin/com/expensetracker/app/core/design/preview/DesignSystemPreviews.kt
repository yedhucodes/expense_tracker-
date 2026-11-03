package com.expensetracker.app.core.design.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.components.AmountDisplayStyle
import com.expensetracker.app.core.design.components.AmountText
import com.expensetracker.app.core.design.components.CategoryIconTile
import com.expensetracker.app.core.design.components.EmptyState
import com.expensetracker.app.core.design.components.GlassNavigationBar
import com.expensetracker.app.core.design.components.NavDestination
import com.expensetracker.app.core.design.components.PrimaryActionButton
import com.expensetracker.app.core.design.components.ScreenHeader
import com.expensetracker.app.core.design.components.SectionHeader
import com.expensetracker.app.core.design.components.TileSize
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme

@Composable
fun DesignSystemPreviewContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.canvas)
            .padding(AppSpacing.md)
    ) {
        ScreenHeader(
            title = "Design System",
            subtitle = "Theme & Component Tokens"
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        SectionHeader(
            title = "Amounts & Currencies",
            actionLabel = "View details",
            onActionClick = {}
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AmountText(
                amountMinor = 142500L,
                currencyCode = "USD",
                isIncome = false,
                includePrefix = true,
                displayStyle = AmountDisplayStyle.Title
            )
            AmountText(
                amountMinor = 350000L,
                currencyCode = "USD",
                isIncome = true,
                includePrefix = true,
                displayStyle = AmountDisplayStyle.Title
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        SectionHeader(title = "Category Tiles")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CategoryRegistry.DefaultExpenseCategories.take(4).forEach { cat ->
                CategoryIconTile(category = cat, size = TileSize.Standard)
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.md))

        PrimaryActionButton(
            text = "Save Expense",
            onClick = {},
            leadingIcon = Icons.Rounded.Add
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        GlassNavigationBar(
            currentDestination = NavDestination.Home,
            onNavigateToDestination = {},
            onAddClick = {}
        )
    }
}

@Preview(name = "Dark Theme - Phone", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DesignSystemDarkPreview() {
    AppTheme(darkTheme = true) {
        DesignSystemPreviewContent()
    }
}

@Preview(name = "Light Theme - Phone", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun DesignSystemLightPreview() {
    AppTheme(darkTheme = false) {
        DesignSystemPreviewContent()
    }
}

