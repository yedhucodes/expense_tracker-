package com.expensetracker.app.core.design.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
 * Top chromatic rainbow arc highlight
 */
private val ChromaticTopRainbow = Brush.horizontalGradient(
    listOf(
        Color.Transparent,
        Color(0xFFFF2E63).copy(alpha = 0.70f),
        Color(0xFFFFB800).copy(alpha = 0.85f),
        Color(0xFF00E5FF).copy(alpha = 0.95f),
        Color(0xFF0077FF).copy(alpha = 0.85f),
        Color(0xFF9D00FF).copy(alpha = 0.70f),
        Color.Transparent
    )
)

/**
 * Bottom subtle chromatic refraction accent
 */
private val ChromaticBottomRainbow = Brush.horizontalGradient(
    listOf(
        Color.Transparent,
        Color(0xFF00E5FF).copy(alpha = 0.40f),
        Color(0xFF0077FF).copy(alpha = 0.55f),
        Color(0xFFFF2E63).copy(alpha = 0.35f),
        Color.Transparent
    )
)

/**
 * Modern BeatWave Liquid Glass Navigation Bar:
 * - Fluid touch & drag response with 1:1 finger tracking across the entire pill
 * - Magnetic bouncy spring snap on finger lift
 * - Proximity-driven micro-interactions (elevation & scale) on icons under the lens
 * - Tactile haptic feedback ticks on crossing tab thresholds
 * - Droplet elongation momentum based on drag velocity
 * - Left 4-tab liquid glass capsule + right standalone (+) floating action button
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

            var isDragging by remember { mutableStateOf(false) }
            var dragOffsetFraction by remember { mutableFloatStateOf(selectedIndex.toFloat()) }
            var lastHapticIndex by remember { mutableIntStateOf(selectedIndex) }

            // Sync with external destination changes (when not actively dragging)
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
                        accentGlow = if (isDragging) Color(0xFF00E5FF).copy(alpha = 0.60f) else Color(0xFF00E5FF),
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

            // Tabs Row with Proximity Micro-Interactions
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(destinations.size) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val widthPx = size.width.toFloat()
                            val tabWidthPx = if (widthPx > 0f) widthPx / destinations.size else 1f

                            val initialIndex = (down.position.x / tabWidthPx)
                                .toInt()
                                .coerceIn(0, destinations.size - 1)

                            isDragging = true
                            dragOffsetFraction = initialIndex.toFloat()
                            lastHapticIndex = initialIndex
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                            val pointerId = down.id
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                                if (change.pressed) {
                                    val currentX = change.position.x
                                    val continuousFraction = ((currentX / tabWidthPx) - 0.5f)
                                        .coerceIn(0f, (destinations.size - 1).toFloat())
                                    dragOffsetFraction = continuousFraction

                                    val currentNearest = (currentX / tabWidthPx)
                                        .toInt()
                                        .coerceIn(0, destinations.size - 1)

                                    if (currentNearest != lastHapticIndex) {
                                        lastHapticIndex = currentNearest
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    change.consume()
                                } else {
                                    // Finger lifted: magnetic snap to the nearest destination!
                                    isDragging = false
                                    val finalIndex = (change.position.x / tabWidthPx)
                                        .toInt()
                                        .coerceIn(0, destinations.size - 1)
                                    dragOffsetFraction = finalIndex.toFloat()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateToDestination(destinations[finalIndex])
                                    break
                                }
                            }
                        }
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    val proximity = (1f - abs(animatedIndex - index)).coerceIn(0f, 1f)
                    NavTabItem(
                        destination = destination,
                        proximity = proximity,
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

/**
 * Tab item displaying an animated icon and title with proximity-based micro-interactions:
 * - Icon floats upward (-2.5dp) into the liquid glass lens as it approaches
 * - Icon gently zooms (1.12f)
 * - Color smoothly brightens to active state
 */
@Composable
private fun NavTabItem(
    destination: NavDestination,
    proximity: Float,
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
