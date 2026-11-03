package com.expensetracker.app.core.design.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

val LocalHazeState = compositionLocalOf { HazeState() }

/**
 * Atmospheric background with vibrant, luminous glowing ambient color mesh orbs
 * matching the signature BeatWave electric aura.
 */
@Composable
fun AtmosphericBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = AppTheme.colors.isDark
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvas)
    ) {
        // Ambient color orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (isDark) {
                // Top BeatWave cyan atmospheric radiant aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x450EA5E9), Color(0x200284C7), Color(0x080369A1), Color.Transparent),
                        center = Offset(w * 0.50f, 0f),
                        radius = (w * 0.95f) * pulse
                    )
                )

                // Top-right indigo/violet orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x386366F1), Color(0x184F46E5), Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.18f),
                        radius = (w * 0.75f) * (2f - pulse)
                    )
                )

                // Mid-left luminous emerald/cyan orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x3210B981), Color(0x14059669), Color.Transparent),
                        center = Offset(w * 0.08f, h * 0.52f),
                        radius = (w * 0.70f) * pulse
                    )
                )

                // Center soft blue ambient bloom
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x183B82F6), Color.Transparent),
                        center = Offset(w * 0.50f, h * 0.45f),
                        radius = w * 0.85f
                    )
                )

                // Bottom-right warm rose/amber orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2EFA5252), Color(0x10E03131), Color.Transparent),
                        center = Offset(w * 0.90f, h * 0.85f),
                        radius = (w * 0.80f) * (2f - pulse)
                    )
                )
            } else {
                // Light mode: soft pastel atmospheric orbs
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x35A5B4FC), Color(0x15C7D2FE), Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.12f),
                        radius = (w * 0.75f) * pulse
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x306EE7B7), Color(0x12A7F3D0), Color.Transparent),
                        center = Offset(w * 0.10f, h * 0.45f),
                        radius = (w * 0.65f) * (2f - pulse)
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x25FDA4AF), Color(0x0CFECDD3), Color.Transparent),
                        center = Offset(w * 0.90f, h * 0.82f),
                        radius = (w * 0.80f) * pulse
                    )
                )
            }
        }

        content()
    }
}

/**
 * Modifier that applies true Liquid Glass styling:
 * - Semi-translucent glass gradient surface (letting ambient lights shine through)
 * - Hairline metallic light reflection border (gradient from top to bottom)
 */
@Composable
fun Modifier.liquidGlass(
    hazeState: HazeState? = null,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = if (AppTheme.colors.isDark) Color(0xD0131B27) else Color(0xEBFFFFFF),
    blurRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    borderTopColor: Color = if (AppTheme.colors.isDark) Color(0x65FFFFFF) else Color(0x99FFFFFF),
    borderBottomColor: Color = if (AppTheme.colors.isDark) Color(0x15FFFFFF) else Color(0x1A000000)
): Modifier {
    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                backgroundColor = if (AppTheme.colors.isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                tint = HazeTint(tint),
                blurRadius = blurRadius
            )
        )
    } else {
        Modifier.background(tint, shape)
    }

    val borderBrush = Brush.verticalGradient(
        colors = listOf(borderTopColor, borderBottomColor)
    )

    return this
        .clip(shape)
        .then(hazeModifier)
        .border(width = borderWidth, brush = borderBrush, shape = shape)
}

/**
 * Frosted Liquid Glass Card container for dashboard cards, budget cards, and insight panels.
 * Renders an Apple Music / BeatWave glass surface with semi-translucent gradient tint and
 * a 1.dp hairline metallic reflection gradient stroke over the atmospheric canvas.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = if (AppTheme.colors.isDark) Color(0xD0131B27) else Color(0xEBFFFFFF),
    borderWidth: Dp = 1.dp,
    borderTopColor: Color = if (AppTheme.colors.isDark) Color(0x65FFFFFF) else Color(0x99FFFFFF),
    borderBottomColor: Color = if (AppTheme.colors.isDark) Color(0x15FFFFFF) else Color(0x1A000000),
    hazeState: HazeState? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = AppTheme.colors.isDark
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(role = Role.Button, onClick = onClick)
    } else Modifier

    val borderBrush = Brush.verticalGradient(
        colors = listOf(borderTopColor, borderBottomColor)
    )

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(Color(0x85212C3C), Color(0x55141B26))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xF5FFFFFF), Color(0xEBFFFFFF))
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush, shape)
            .border(width = borderWidth, brush = borderBrush, shape = shape)
            .then(clickableModifier)
    ) {
        // Top Specular Highlight Gloss Line
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            if (isDark) Color(0x50FFFFFF) else Color(0x80FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )

        content()
    }
}
