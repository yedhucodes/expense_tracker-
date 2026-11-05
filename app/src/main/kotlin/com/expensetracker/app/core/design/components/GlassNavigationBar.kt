package com.expensetracker.app.core.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
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
import com.expensetracker.app.core.design.theme.MotionTokens
import com.expensetracker.app.core.design.theme.PillShape
import com.expensetracker.app.core.design.theme.ShuDingSpecularRimBrush
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

/** Maps destination to 5-slot navigation index: [Home, Activity, Add(+), Insights, Budgets] */
private fun destinationToSlotIndex(destination: NavDestination): Int = when (destination) {
    NavDestination.Home -> 0
    NavDestination.Activity -> 1
    NavDestination.Insights -> 3
    NavDestination.Budgets -> 4
}

/** Maps 5-slot navigation index to destination (returns null for center Add button slot) */
private fun slotIndexToDestination(slot: Int): NavDestination? = when (slot) {
    0 -> NavDestination.Home
    1 -> NavDestination.Activity
    3 -> NavDestination.Insights
    4 -> NavDestination.Budgets
    else -> null
}

/**
 * Modern Liquid Glass Navigation Bar:
 * Inspired by Dribbble Tab Bar Animation & Apple Liquid Glass Design System.
 *
 * Structure:
 * 1. Home
 * 2. Activity
 * 3. Central Add Transaction action (+)
 * 4. Insights
 * 5. Budgets
 *
 * - Direct tap response on any tab (guaranteed Home / Tab navigation)
 * - Fluid touch & drag response with 1:1 finger tracking across the entire pill
 * - Magnetic bouncy spring snap on finger lift
 * - Proximity-driven micro-interactions (elevation & scale) on icons under the lens
 * - Tactile haptic feedback ticks on crossing tab thresholds
 * - Droplet elongation momentum based on motion velocity
 * - Central (+) action button with restrained Indigo/Violet gradient and reactive spring scale
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
    val currentSlot = destinationToSlotIndex(currentDestination)
    val haptic = LocalHapticFeedback.current
    val accentColor = AppTheme.colors.accentPrimary
    val violetColor = AppTheme.colors.violetHighlight

    val addInteractionSource = remember { MutableInteractionSource() }
    val isAddPressed by addInteractionSource.collectIsPressedAsState()
    val addScale by animateFloatAsState(
        targetValue = if (isAddPressed) 0.88f else 1f,
        animationSpec = MotionTokens.FabPressSpring,
        label = "add_press_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Unified 5-Slot Floating Navigation Capsule
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shuDingLiquidGlass(
                    shape = CircleShape,
                    isDark = isDark,
                    borderWidth = 1.dp
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val slotCount = 5
            val slotWidth = totalWidth / slotCount
            val density = LocalDensity.current
            val totalWidthPx = with(density) { totalWidth.toPx() }

            var isDragging by remember { mutableStateOf(false) }
            var dragSlotFraction by remember { mutableFloatStateOf(currentSlot.toFloat()) }
            var lastHapticSlot by remember { mutableIntStateOf(currentSlot) }

            // Sync with external destination changes whenever not actively dragging
            LaunchedEffect(currentSlot) {
                if (!isDragging) {
                    dragSlotFraction = currentSlot.toFloat()
                    lastHapticSlot = currentSlot
                }
            }

            // Real-time animation: tight tracking while dragging, bouncy spring snap when resting
            val targetSlot = if (isDragging) dragSlotFraction else currentSlot.toFloat()
            val animatedSlot by animateFloatAsState(
                targetValue = targetSlot,
                animationSpec = if (isDragging) {
                    MotionTokens.DragTrackingSpring
                } else {
                    MotionTokens.IndicatorSpring
                },
                label = "tab_hover_slide"
            )

            // Droplet elongation momentum: lens stretches along the motion vector
            val slideDelta = (animatedSlot - (if (isDragging) dragSlotFraction else currentSlot.toFloat())).coerceIn(-0.6f, 0.6f)
            val fluidStretchX = 1f + abs(slideDelta) * 0.16f + (if (isDragging) 0.08f else 0f)

            // Sliding Active Glass Capsule Indicator (glides between destination slots)
            Box(
                modifier = Modifier
                    .offset(x = slotWidth * animatedSlot)
                    .width(slotWidth)
                    .height(54.dp)
                    .graphicsLayer {
                        scaleX = fluidStretchX
                    }
                    .padding(horizontal = 3.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDark) Color.Transparent else accentColor.copy(alpha = 0.14f),
                        CircleShape
                    )
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

            // 5-Slot Interactive Row (Slot 0: Home, 1: Activity, 2: Center FAB, 3: Insights, 4: Budgets)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        val slotWidthPx = if (totalWidthPx > 0f) totalWidthPx / slotCount else 1f

                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                isDragging = true
                                val fraction = (offset.x / slotWidthPx - 0.5f).coerceIn(0f, 4f)
                                dragSlotFraction = fraction
                                val nearestSlot = (offset.x / slotWidthPx).toInt().coerceIn(0, 4)
                                lastHapticSlot = nearestSlot
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDragEnd = {
                                isDragging = false
                                val rawSlot = (dragSlotFraction + 0.5f).toInt().coerceIn(0, 4)
                                // If released on slot 2 (center button), resolve to closest destination
                                val resolvedDestination = when (rawSlot) {
                                    0 -> NavDestination.Home
                                    1 -> NavDestination.Activity
                                    2 -> if (dragSlotFraction < 2.0f) NavDestination.Activity else NavDestination.Insights
                                    3 -> NavDestination.Insights
                                    4 -> NavDestination.Budgets
                                    else -> NavDestination.Home
                                }
                                val finalSlot = destinationToSlotIndex(resolvedDestination)
                                dragSlotFraction = finalSlot.toFloat()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToDestination(resolvedDestination)
                            },
                            onDragCancel = {
                                isDragging = false
                                dragSlotFraction = currentSlot.toFloat()
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newFraction = (dragSlotFraction + dragAmount / slotWidthPx).coerceIn(0f, 4f)
                                dragSlotFraction = newFraction
                                val currentNearest = (newFraction + 0.5f).toInt().coerceIn(0, 4)
                                if (currentNearest != lastHapticSlot && currentNearest != 2) {
                                    lastHapticSlot = currentNearest
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slot 0: Home
                NavTabItem(
                    destination = NavDestination.Home,
                    proximity = (1f - abs(animatedSlot - 0f)).coerceIn(0f, 1f),
                    onClick = {
                        isDragging = false
                        dragSlotFraction = 0f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToDestination(NavDestination.Home)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Slot 1: Activity
                NavTabItem(
                    destination = NavDestination.Activity,
                    proximity = (1f - abs(animatedSlot - 1f)).coerceIn(0f, 1f),
                    onClick = {
                        isDragging = false
                        dragSlotFraction = 1f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToDestination(NavDestination.Activity)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Slot 2: Central Add Transaction Action Button (+)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .graphicsLayer {
                                scaleX = addScale
                                scaleY = addScale
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        accentColor,
                                        violetColor
                                    )
                                )
                            )
                            .border(1.dp, ShuDingSpecularRimBrush, CircleShape)
                            .clickable(
                                interactionSource = addInteractionSource,
                                indication = null,
                                role = Role.Button,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onAddClick()
                                }
                            )
                            .semantics { contentDescription = "Add new transaction" },
                        contentAlignment = Alignment.Center
                    ) {
                        // Top diamond reflection highlight
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(2.dp)
                                .align(Alignment.TopCenter)
                                .background(DiamondSpecularHighlight)
                        )

                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )

                        // Bottom rim
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.50f)
                                .height(1.dp)
                                .align(Alignment.BottomCenter)
                                .background(DiamondSpecularRimBottom)
                        )
                    }
                }

                // Slot 3: Insights
                NavTabItem(
                    destination = NavDestination.Insights,
                    proximity = (1f - abs(animatedSlot - 3f)).coerceIn(0f, 1f),
                    onClick = {
                        isDragging = false
                        dragSlotFraction = 3f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToDestination(NavDestination.Insights)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Slot 4: Budgets
                NavTabItem(
                    destination = NavDestination.Budgets,
                    proximity = (1f - abs(animatedSlot - 4f)).coerceIn(0f, 1f),
                    onClick = {
                        isDragging = false
                        dragSlotFraction = 4f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToDestination(NavDestination.Budgets)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Tab item displaying an animated icon and title with proximity-based micro-interactions:
 * - Icon floats upward (-3dp) and zooms (1.14f) as active liquid glass lens approaches
 * - Smooth color brightening to crisp active white state
 * - Label fades in gracefully on active state
 * - Direct tap handler ensures immediate, rock-solid navigation
 */
@Composable
private fun NavTabItem(
    destination: NavDestination,
    proximity: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = AppTheme.colors.isDark

    val activeContentColor = if (isDark) Color.White else AppTheme.colors.accentPrimary
    val inactiveContentColor = if (isDark) Color(0x9994A3B8) else Color(0xFF64748B)

    // Smooth color interpolation based on proximity to active liquid lens
    val contentColor = Color(
        red = inactiveContentColor.red + (activeContentColor.red - inactiveContentColor.red) * proximity,
        green = inactiveContentColor.green + (activeContentColor.green - inactiveContentColor.green) * proximity,
        blue = inactiveContentColor.blue + (activeContentColor.blue - inactiveContentColor.blue) * proximity,
        alpha = inactiveContentColor.alpha + (activeContentColor.alpha - inactiveContentColor.alpha) * proximity
    )

    val iconScale = 1f + proximity * 0.14f
    val iconElevation = (-3.0f * proximity).dp
    val isSelected = proximity > 0.6f

    Box(
        modifier = modifier
            .height(54.dp)
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
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
