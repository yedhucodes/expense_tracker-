package com.expensetracker.app.core.design.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.shuDingLiquidGlass
import dev.chrisbanes.haze.HazeState
import kotlin.math.abs

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

/**
 * Pure diamond specular reflection highlight - clean, crystalline, and restrained.
 * Harmonizes with Apple Liquid Glass aesthetic without clashing rainbow bars.
 */
private val DiamondSpecularHighlight = Brush.horizontalGradient(
    listOf(
        Color.Transparent,
        Color(0x60FFFFFF),
        Color(0xD0FFFFFF),
        Color(0x60FFFFFF),
        Color.Transparent
    )
)

private val DiamondSpecularRimBottom = Brush.horizontalGradient(
    listOf(
        Color.Transparent,
        Color(0x25FFFFFF),
        Color(0x50FFFFFF),
        Color(0x25FFFFFF),
        Color.Transparent
    )
)

/**
 * Modern Liquid Glass Navigation Bar:
 * - Direct tap response on any tab (guaranteed Home / Tab navigation)
 * - Fluid touch & drag response with 1:1 finger tracking across the entire pill
 * - Magnetic bouncy spring snap on finger lift
 * - Proximity-driven micro-interactions (elevation & scale) on icons under the lens
 * - Tactile haptic feedback ticks on crossing tab thresholds
 * - Droplet elongation momentum based on motion velocity
 * - Left 4-tab liquid glass capsule + right standalone (+) floating action button
 * - Cohesive Obsidian & Royal Indigo palette matching the entire application
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
    val destinations = remember { NavDestination.entries }
    val selectedIndex = destinations.indexOf(currentDestination).coerceAtLeast(0)
    val haptic = LocalHapticFeedback.current
    val accentColor = AppTheme.colors.accentPrimary

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
        // Main Navigation Capsule Pill with Interactive Touch & Drag Tracking
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .height(64.dp)
                .shuDingLiquidGlass(
                    shape = CircleShape,
                    isDark = isDark,
                    borderWidth = 1.dp
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / destinations.size
            val density = LocalDensity.current
            val totalWidthPx = with(density) { totalWidth.toPx() }

            var isDragging by remember { mutableStateOf(false) }
            var dragOffsetFraction by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
            var lastHapticIndex by remember { mutableIntStateOf(selectedIndex) }

            // Sync with external destination changes whenever not actively dragging
            LaunchedEffect(selectedIndex) {
                if (!isDragging) {
                    dragOffsetFraction = selectedIndex.toFloat()
                    lastHapticIndex = selectedIndex
                }
            }

            // Real-time animation: tight tracking while dragging, bouncy spring snap when resting
            val targetIndex = if (isDragging) dragOffsetFraction else selectedIndex.toFloat()
            val animatedIndex by animateFloatAsState(
                targetValue = targetIndex,
                animationSpec = if (isDragging) {
                    spring(
                        dampingRatio = 0.95f,
                        stiffness = Spring.StiffnessHigh
                    )
                } else {
                    spring(
                        dampingRatio = 0.72f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                },
                label = "tab_hover_slide"
            )

            // Droplet elongation momentum: lens stretches along the motion vector
            val slideDelta = (animatedIndex - (if (isDragging) dragOffsetFraction else selectedIndex.toFloat())).coerceIn(-0.6f, 0.6f)
            val fluidStretchX = 1f + abs(slideDelta) * 0.16f + (if (isDragging) 0.08f else 0f)

            // Sliding Active Glass Capsule with Shu Ding Optical Refraction & Specular Bloom
            Box(
                modifier = Modifier
                    .offset(x = tabWidth * animatedIndex)
                    .width(tabWidth)
                    .height(52.dp)
                    .graphicsLayer {
                        scaleX = fluidStretchX
                    }
                    .padding(horizontal = 2.dp)
                    .shuDingLiquidGlass(
                        shape = CircleShape,
                        isDark = isDark,
                        accentGlow = accentColor,
                        borderWidth = 1.dp
                    )
            ) {
                // Top Diamond Specular Highlight Glint
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(2.dp)
                        .align(Alignment.TopCenter)
                        .background(DiamondSpecularHighlight)
                )

                // Bottom Subtle Specular Rim
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(1.5.dp)
                        .align(Alignment.BottomCenter)
                        .background(DiamondSpecularRimBottom)
                )
            }

            // Tabs Row with Proximity Micro-Interactions + Drag Gesture Detector
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(destinations.size) {
                        val tabWidthPx = if (totalWidthPx > 0f) totalWidthPx / destinations.size else 1f

                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                isDragging = true
                                val fraction = (offset.x / tabWidthPx - 0.5f)
                                    .coerceIn(0f, (destinations.size - 1).toFloat())
                                dragOffsetFraction = fraction
                                val nearest = (offset.x / tabWidthPx).toInt().coerceIn(0, destinations.size - 1)
                                lastHapticIndex = nearest
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDragEnd = {
                                isDragging = false
                                val finalIndex = (dragOffsetFraction + 0.5f).toInt().coerceIn(0, destinations.size - 1)
                                dragOffsetFraction = finalIndex.toFloat()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToDestination(destinations[finalIndex])
                            },
                            onDragCancel = {
                                isDragging = false
                                dragOffsetFraction = selectedIndex.toFloat()
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newFraction = (dragOffsetFraction + dragAmount / tabWidthPx)
                                    .coerceIn(0f, (destinations.size - 1).toFloat())
                                dragOffsetFraction = newFraction
                                val currentNearest = (newFraction + 0.5f).toInt().coerceIn(0, destinations.size - 1)
                                if (currentNearest != lastHapticIndex) {
                                    lastHapticIndex = currentNearest
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    val proximity = (1f - abs(animatedIndex - index)).coerceIn(0f, 1f)
                    NavTabItem(
                        destination = destination,
                        proximity = proximity,
                        onClick = {
                            isDragging = false
                            dragOffsetFraction = index.toFloat()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToDestination(destination)
                        },
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
                    accentGlow = accentColor,
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
            // Top Diamond Specular Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.60f)
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .background(DiamondSpecularHighlight)
            )

            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )

            // Bottom Diamond Specular Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
                    .height(1.5.dp)
                    .align(Alignment.BottomCenter)
                    .background(DiamondSpecularRimBottom)
            )
        }
    }
}

/**
 * Tab item displaying an animated icon and title with proximity-based micro-interactions:
 * - Icon floats upward (-2.5dp) into the liquid glass lens as it approaches
 * - Icon gently zooms (1.12f)
 * - Color smoothly brightens to active white state
 * - Direct tap handler ensures immediate, reliable navigation
 */
@Composable
private fun NavTabItem(
    destination: NavDestination,
    proximity: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = AppTheme.colors.isDark

    val activeContentColor = if (isDark) Color.White else Color(0xFF0F172A)
    val inactiveContentColor = if (isDark) Color(0x9994A3B8) else Color(0xFF64748B)

    // Smooth color interpolation based on proximity to the active liquid lens
    val contentColor = Color(
        red = inactiveContentColor.red + (activeContentColor.red - inactiveContentColor.red) * proximity,
        green = inactiveContentColor.green + (activeContentColor.green - inactiveContentColor.green) * proximity,
        blue = inactiveContentColor.blue + (activeContentColor.blue - inactiveContentColor.blue) * proximity,
        alpha = inactiveContentColor.alpha + (activeContentColor.alpha - inactiveContentColor.alpha) * proximity
    )

    val iconScale = 1f + proximity * 0.12f
    val iconElevation = (-2.5f * proximity).dp

    Box(
        modifier = modifier
            .height(52.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = destination.title },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
                translationY = iconElevation.toPx()
            }
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
                fontWeight = if (proximity > 0.6f) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}