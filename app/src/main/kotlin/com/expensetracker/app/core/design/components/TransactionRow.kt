package com.expensetracker.app.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.theme.AppShapes
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDate: Boolean = false
) {
    val category = CategoryRegistry.getCategory(transaction.categoryId)
    val hasMerchant = !transaction.merchant.isNullOrBlank()
    val primaryTitle = transaction.merchant?.ifBlank { null } ?: category.name

    val subtitleParts = mutableListOf<String>()
    if (hasMerchant) {
        subtitleParts.add(category.name)
    }
    if (showDate) {
        subtitleParts.add(DatePeriodUtil.formatMediumDate(transaction.occurredAt))
    } else {
        subtitleParts.add(DatePeriodUtil.formatTime(transaction.occurredAt))
    }
    transaction.paymentMethod?.let {
        if (it.isNotBlank()) subtitleParts.add(it)
    }
    val subtitleText = subtitleParts.joinToString(" • ")

    val isIncome = transaction.type == TransactionType.INCOME
    val accessibilityLabel = buildString {
        append(if (isIncome) "Income" else "Expense")
        append(": $primaryTitle, ")
        append(subtitleText)
        append(", amount ")
        append(if (isIncome) "+" else "-")
        append(transaction.amountMinor / 100.0)
        append(" ${transaction.currencyCode}")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .clip(AppShapes.medium)
            .background(if (AppTheme.colors.isDark) Color(0x55151D2A) else Color(0x80FFFFFF))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        if (AppTheme.colors.isDark) Color(0x35FFFFFF) else Color(0x90FFFFFF),
                        if (AppTheme.colors.isDark) Color(0x0CFFFFFF) else Color(0x20000000)
                    )
                ),
                shape = AppShapes.medium
            )
            .clickable(
                role = Role.Button,
                onClickLabel = "Edit transaction $primaryTitle",
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityLabel
            }
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIconTile(
            iconKey = category.iconKey,
            color = category.color,
            size = TileSize.Standard
        )

        Spacer(modifier = Modifier.width(AppSpacing.md))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = primaryTitle,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitleText,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!transaction.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.note,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(AppSpacing.sm))

        AmountText(
            amountMinor = transaction.amountMinor,
            currencyCode = transaction.currencyCode,
            isIncome = isIncome,
            includePrefix = true,
            displayStyle = AmountDisplayStyle.ListAmount
        )
    }
}

