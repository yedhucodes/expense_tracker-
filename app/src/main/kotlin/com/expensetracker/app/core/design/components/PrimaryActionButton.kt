package com.expensetracker.app.core.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.MotionTokens
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.ShuDingLightRimBrush
import com.expensetracker.app.core.design.theme.ShuDingSpecularRimBrush

/**
 * Liquid glass primary action button:
 * - High-contrast, vibrant call-to-action fill in both Dark and Light themes
 * - Tactile spring compression on touch
 * - Pure diamond specular apex sheen
 * - Full accessibility compliance with distinct enabled/disabled states
 */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    backgroundColor: Color = AppTheme.colors.accentPrimary,
    contentColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDark = AppTheme.colors.isDark

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.95f else 1f,
        animationSpec = MotionTokens.FabPressSpring,
        label = "primary_button_press_scale"
    )

    val buttonBrush = if (enabled) {
        Brush.linearGradient(
            colors = listOf(
                backgroundColor,
                AppTheme.colors.violetHighlight
            )
        )
    } else {
        SolidColor(if (isDark) Color(0xFF1E2433) else Color(0xFFE2E8F0))
    }

    val actualContentColor = if (enabled) {
        contentColor
    } else {
        if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
    }

    val borderBrush = if (isDark) {
        ShuDingSpecularRimBrush
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0x60FFFFFF),
                Color(0x25000000)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(PillShape)
            .background(buttonBrush, PillShape)
            .border(width = 1.dp, brush = borderBrush, shape = PillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = AppSpacing.xxl, vertical = AppSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        // Specular apex glint for liquid glass sheen
        Box(
            modifier = Modifier
                .fillMaxWidth(0.70f)
                .height(1.5.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = if (enabled) 0.50f else 0.20f),
                            Color.Transparent
                        )
                    )
                )
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = actualContentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = actualContentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                }
                Text(
                    text = text,
                    style = AppTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = actualContentColor
                )
            }
        }
    }
}

/**
 * Secondary liquid glass pill button:
 * - High-contrast surface in both Dark and Light themes
 * - Translucent obsidian in dark mode, crisp porcelain with defining border in light mode
 */
@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDark = AppTheme.colors.isDark

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = MotionTokens.FabPressSpring,
        label = "secondary_button_press_scale"
    )

    val surfaceBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(Color(0x351B1F2D), Color(0x20131620))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))
        )
    }

    val borderBrush = if (isDark) {
        ShuDingSpecularRimBrush
    } else {
        Brush.verticalGradient(
            listOf(Color(0x40FFFFFF), Color(0x30000000))
        )
    }

    val textColor = if (enabled) {
        if (isDark) Color.White else AppTheme.colors.textPrimary
    } else {
        AppTheme.colors.textSecondary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(PillShape)
            .background(surfaceBrush, PillShape)
            .border(width = 1.dp, brush = borderBrush, shape = PillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = AppSpacing.xxl, vertical = AppSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
            }
            Text(
                text = text,
                style = AppTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}
