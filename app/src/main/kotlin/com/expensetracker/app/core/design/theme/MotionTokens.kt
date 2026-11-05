package com.expensetracker.app.core.design.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Reusable motion tokens tuned against the Obsidian Indigo liquid design system
 * and interaction targets defined in the UI Redesign Build Plan.
 */
object MotionTokens {
    // Target durations in milliseconds
    const val DurationIndicator = 280
    const val DurationIconEmphasis = 200
    const val DurationLabelReveal = 180
    const val DurationFabPress = 160
    const val DurationScreenTransition = 250
    const val DurationCardExpand = 220

    // Fluid indicator spring: smooth travel with minimal overshoot
    val IndicatorSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMediumLow
    )

    // Touch & drag tracking: tight 1:1 adherence under finger
    val DragTrackingSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.95f,
        stiffness = Spring.StiffnessHigh
    )

    // Interactive press feedback on buttons and FAB
    val FabPressSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.65f,
        stiffness = Spring.StiffnessMedium
    )

    // Icon micro-emphasis scale spring
    val IconScaleSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = Spring.StiffnessMedium
    )

    // Standard reveal tweens
    val LabelFadeIn: TweenSpec<Float> = tween(
        durationMillis = DurationLabelReveal,
        easing = FastOutSlowInEasing
    )

    val ScreenTransition: TweenSpec<Float> = tween(
        durationMillis = DurationScreenTransition,
        easing = FastOutSlowInEasing
    )
}

