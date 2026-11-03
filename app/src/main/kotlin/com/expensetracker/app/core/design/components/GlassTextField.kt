package com.expensetracker.app.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme

/**
 * Liquid Glass styled text input field:
 * - Seamless frosted glass container with 16dp rounded corners
 * - Luminous metallic gradient border that illuminates on focus
 * - Clean integrated leading icon, typography, and optional trailing action
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val isDark = AppTheme.colors.isDark
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val accentColor = if (isError) AppTheme.colors.expense else AppTheme.colors.accentPrimary

    val borderTopColor by animateColorAsState(
        targetValue = when {
            isError -> AppTheme.colors.expense
            isFocused -> accentColor
            isDark -> Color(0x55FFFFFF)
            else -> Color(0x80FFFFFF)
        },
        label = "glass_tf_border_top"
    )

    val borderBottomColor by animateColorAsState(
        targetValue = when {
            isError -> AppTheme.colors.expense.copy(alpha = 0.4f)
            isFocused -> accentColor.copy(alpha = 0.4f)
            isDark -> Color(0x15FFFFFF)
            else -> Color(0x1A000000)
        },
        label = "glass_tf_border_bottom"
    )

    val containerBg = when {
        isFocused -> if (isDark) Color(0x501C2638) else Color(0xF5FFFFFF)
        isDark -> Color(0x35141D2B)
        else -> Color(0x85FFFFFF)
    }

    val borderBrush = Brush.verticalGradient(listOf(borderTopColor, borderBottomColor))

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = AppTheme.typography.labelMedium,
                color = if (isFocused) accentColor else AppTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(containerBg, shape)
                .border(if (isFocused || isError) 1.5.dp else 1.dp, borderBrush, shape)
                .padding(horizontal = AppSpacing.md, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isFocused) accentColor else AppTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.md))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = AppTheme.typography.bodyLarge,
                            color = if (isDark) Color(0x6094A3B8) else Color(0x7064748B)
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = singleLine,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        interactionSource = interactionSource,
                        textStyle = AppTheme.typography.bodyLarge.copy(
                            color = AppTheme.colors.textPrimary,
                            fontSize = 15.sp
                        ),
                        cursorBrush = SolidColor(accentColor)
                    )
                }

                if (trailingContent != null) {
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    trailingContent()
                }
            }
        }

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.expense,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }
    }
}
