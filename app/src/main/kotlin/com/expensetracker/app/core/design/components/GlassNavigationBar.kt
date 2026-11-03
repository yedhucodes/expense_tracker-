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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.liquidGlass
import com.expensetracker.app.core.design.theme.shuDingLiquidGlass
import dev.chrisbanes.haze.HazeState

enum class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Home("home", "Home", Icons.Rounded.Home),
    Activity("activity", "Activity", Icons.AutoMirrored.Rounded.ReceiptLong),
    Insights("insights", "Insights", Icons.Rounded.BarChart),
    Budgets("budgets", "Budgets", Icons.Rounded.AccountBalanceWallet)
}

// Chromatic Aberration / Liquid Prismatic Refraction Highlights (BeatWave style)
private val ChromaticTopRainbow = Brush.horizontalGradient(
    listOf(
        Color(0x00FFAA00),
        Color(0xEEFFAA00), // Amber
        Color(0xFFFF2E63), // Pink/Magenta
        Color(0xFFA855F7), // Violet
        Color(0xFF00E5FF), // Cyan
        Color(0xEE0077FF), // Electric Blue
        Color(0x000077FF)
    )
)

private val ChromaticBottomRainbow = Brush.horizontalGradient(
    listOf(
        Color(0x000077FF),
        Color(0xDD0077FF), // Electric Blue
        Color(0xFF00E5FF), // Cyan
        Color(0xEEFFFFFF), // Specular White
        Color(0xFFFF2E63), // Magenta
        Color(0xDDFFAA00), // Amber
        Color(0x00FFAA00)
    )
)

/**
 * Modern floating glass navigation bar styled exactly after BeatWave:
 * - A primary floating capsule pill on the left containing the navigation destinations.
 * - An animated reactive sliding indicator capsule with spring momentum and tactile press response.
 * - Chromatic liquid glass refraction rainbow rim highlights along the top and bottom borders.
 * - A standalone circular floating glass button on the right for quick Add Transaction.
 */
@Composable
fun GlassNavigationBar(
    currentDestination: NavDestination,
    onNavigateToDestination: (NavDestination) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val isDark = AppTheme.colors.isDark
    val glassTint = if (isDark) Color(0xD0131B27) else Color(0xEBFFFFFF)
    val borderTop = if (isDark) Color(0x55FFFFFF) else Color(0x99FFFFFF)
    val borderBottom = if (isDark) Color(0x12FFFFFF) else Color(0x1A000000)

    val destinations = remember { NavDestination.entries }
    val selectedIndex = destinations.indexOf(currentDestination).coerceAtLeast(0)

    val addInteractionSource = remember { MutableInteractionSource() }
    val isAddPressed by addInteractionSource.collectIsPressedAsState()
    val addScale by animateFloatAsState(
        targetValue = if (isAddPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "add_press_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main Navigation Capsule Pill with Shu Ding Liquid Glass Refraction
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .height(64.dp)
                .shuDingLiquidGlass(
                    shape = CircleShape,
                    isDark = isDark,
                    borderWidth = 1.dp
                )
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / destinations.size

            // Smooth sliding indicator animation with spring physics (Shu Ding damping curve)
            val animatedIndex by animateFloatAsState(
                targetValue = selectedIndex.toFloat(),
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "tab_hover_slide"
            )

            // Sliding Active Glass Capsule with Shu Ding Optical Refraction & Chromatic Rim
            Box(
                modifier = Modifier
                    .offset(x = tabWidth * animatedIndex)
                    .width(tabWidth)
                    .height(52.dp)
                    .padding(horizontal = 2.dp)
                    .shuDingLiquidGlass(
                        shape = CircleShape,
                        isDark = isDark,
                        accentGlow = Color(0xFF00E5FF),
                        borderWidth = 1.dp
                    )
            ) {
                // Top Chromatic Rainbow Lens Flare Arc
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(3.dp)
                        .align(Alignment.TopCenter)
                        .background(ChromaticTopRainbow)
                )

                // Bottom Chromatic Refraction Arc
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(2.dp)
                        .align(Alignment.BottomCenter)
                        .background(ChromaticBottomRainbow)
                )
            }

            // Tabs Row
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { destination ->
                    NavTabItem(
                        destination = destination,
                        isSelected = currentDestination == destination,
                        onClick = { onNavigateToDestination(destination) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Standalone Circular Floating Action Button with Reactive Spring Press & Shu Ding Refraction
        Box(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    scaleX = addScale
                    scaleY = addScale
                }
                .shuDingLiquidGlass(
                    shape = CircleShape,
                    isDark = isDark,
                    accentGlow = if (isDark) Color(0xFF00E5FF) else AppTheme.colors.accentPrimary,
                    borderWidth = 1.dp
                )
                .clickable(
                    interactionSource = addInteractionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onAddClick
                )
                .semantics { contentDescription = "Add new transaction" },
            contentAlignment = Alignment.Center
        ) {
            // Subtle Top Chromatic Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.60f)
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .background(ChromaticTopRainbow)
            )

            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = if (isDark) Color.White else AppTheme.colors.accentPrimary,
                modifier = Modifier.size(28.dp)
            )

            // Subtle Bottom Chromatic Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.60f)
                    .height(2.dp)
                    .align(Alignment.BottomCenter)
                    .background(ChromaticBottomRainbow)
            )
        }
    }
}

@Composable
private fun NavTabItem(
    destination: NavDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = AppTheme.colors.isDark
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile Reactive Press Scaling
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tab_press_scale"
    )

    val activeContentColor = if (isDark) Color.White else Color(0xFF0F172A)
    val inactiveContentColor = if (isDark) Color(0x9994A3B8) else Color(0xFF64748B)

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) activeContentColor else inactiveContentColor,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tab_content_color"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = destination.title },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = destination.title,
                style = AppTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
