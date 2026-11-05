package com.expensetracker.app.core.design.components

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.animation.LottieIllustration
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.shuDingLiquidGlass

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    @RawRes animationRes: Int? = null,
    actionButtonText: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    useSecondaryAction: Boolean = false
) {
    val buttonText = actionButtonText ?: actionLabel
    val isDark = AppTheme.colors.isDark

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (animationRes != null) {
            // Expressive animated Lottie illustration
            LottieIllustration(
                rawRes = animationRes,
                size = 110.dp,
                modifier = Modifier.padding(bottom = AppSpacing.sm)
            )
        } else {
            // Glowing Circular Liquid Glass Orb with Shu Ding Refraction
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shuDingLiquidGlass(
                        shape = CircleShape,
                        isDark = isDark,
                        accentGlow = AppTheme.colors.accentPrimary,
                        borderWidth = 1.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDark) Color.White else AppTheme.colors.accentPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        Text(
            text = title,
            style = AppTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        Text(
            text = description,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppSpacing.md)
        )

        if (buttonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            if (useSecondaryAction) {
                SecondaryActionButton(
                    text = buttonText,
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(0.65f)
                )
            } else {
                PrimaryActionButton(
                    text = buttonText,
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(0.65f)
                )
            }
        }
    }
}
