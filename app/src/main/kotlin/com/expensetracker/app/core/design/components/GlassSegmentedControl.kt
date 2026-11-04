package com.expensetracker.app.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.ShuDingSpecularRimBrush
import com.expensetracker.app.core.design.theme.liquidGlass
import com.expensetracker.app.core.design.theme.shuDingLiquidGlass

/**
 * Modern reactive Liquid Glass Segmented Control:
 * - Fluid sliding indicator with spring physics
 * - Full-height pill container that never vertically compresses
 * - Metallic reflection specular borders and ambient glowing fills
 * - Tactile spring touch response on tab tap
 */
@Composable
fun <T> GlassSegmentedControl(
    items: List<T>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    itemIcon: ((T) -> ImageVector?)? = null,
    activeColor: ((T) -> Color)? = null,
    height: Dp = 48.dp
) {
    if (items.isEmpty()) return

    val isDark = AppTheme.colors.isDark
    val glassTint = if (isDark) Color(0xD0131B27) else Color(0xEBFFFFFF)
    val borderTop = if (isDark) Color(0x55FFFFFF) else Color(0x99FFFFFF)
    val borderBottom = if (isDark) Color(0x12FFFFFF) else Color(0x1A000000)

    val validIndex = selectedIndex.coerceIn(0, items.size - 1)
    val currentItem = items[validIndex]
    val currentAccentColor = activeColor?.invoke(currentItem) ?: AppTheme.colors.accentPrimary

    // Smooth sliding animation
    val animatedIndex by animateFloatAsState(
        targetValue = validIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "segmented_slide"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shuDingLiquidGlass(
                shape = PillShape,
                isDark = isDark,
                borderWidth = 1.dp
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidth = maxWidth
        val itemWidth = totalWidth / items.size

        // Shu Ding fluid momentum: droplet lens elastically elongates horizontally while in transit
        val slideDelta = (animatedIndex - validIndex.toFloat()).coerceIn(-0.6f, 0.6f)
        val fluidStretchX = 1f + kotlin.math.abs(slideDelta) * 0.12f

        // Sliding Active Indicator Capsule with Shu Ding Liquid Glass Refraction
        Box(
            modifier = Modifier
                .offset(x = itemWidth * animatedIndex)
                .width(itemWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    scaleX = fluidStretchX
                }
                .padding(horizontal = 2.dp)
                .shuDingLiquidGlass(
                    shape = PillShape,
                    isDark = isDark,
                    accentGlow = currentAccentColor,
                    borderWidth = 1.dp
                )
        ) {
            // Subtle Top Chromatic Specular Flare
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFFFF2E63).copy(alpha = 0.7f),
                                Color(0xFF00E5FF).copy(alpha = 0.9f),
                                Color(0xFF0077FF).copy(alpha = 0.7f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Labels / Icons Row with Inset Dividers Between Inactive Joined Buttons
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == validIndex
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.94f else 1f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                    label = "tab_scale_$index"
                )

                val itemColor = activeColor?.invoke(item) ?: AppTheme.colors.accentPrimary
                val targetTextColor = when {
                    isSelected -> if (isDark) Color.White else itemColor
                    else -> if (isDark) Color(0x99CBD5E1) else Color(0xFF64748B)
                }

                val animatedTextColor by animateColorAsState(
                    targetValue = targetTextColor,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "text_color_$index"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(PillShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Tab
                        ) {
                            onItemSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val icon = itemIcon?.invoke(item)
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = animatedTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = itemLabel(item),
                            style = AppTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = animatedTextColor
                        )
                    }
                }
            }
        }
    }
}
