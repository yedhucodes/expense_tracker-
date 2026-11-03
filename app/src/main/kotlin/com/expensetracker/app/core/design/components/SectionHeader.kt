package com.expensetracker.app.core.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        androidx.compose.foundation.layout.Column {
            Text(
                text = title,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textSecondary
                )
            }
        }

        if (actionLabel != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                    .clickable(role = Role.Button, onClick = onActionClick)
                    .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = actionLabel,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colors.accentPrimary
                )
            }
        }
    }
}

