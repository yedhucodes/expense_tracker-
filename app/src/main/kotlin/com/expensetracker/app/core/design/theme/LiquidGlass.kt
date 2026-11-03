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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
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

/**
 * Shu Ding's Liquid Glass Specular Rim Brush:
 * Derived from linear-gradient(145deg, rgba(255, 255, 255, 0.72), rgba(205, 246, 255, 0.18) 42%, rgba(255, 255, 255, 0.04) 78%, rgba(255, 255, 255, 0.24))
 */
val ShuDingSpecularRimBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xB8FFFFFF), // 0.72 specular white
        Color(0x35BAF4FF), // 0.18 pale cyan bloom
        Color(0x0CFFFFFF), // 0.04 clear specular
        Color(0x40BAF4FF)  // 0.24 bottom reflection
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

/**
 * Modifier applying Shu Ding's physical Liquid Glass optical model:
 * 1. Dual-layer shell gradients (140deg refraction angle + 180deg depth angle)
 * 2. Inset bottom bevel shadow: simulates box-shadow: 0 -10px 25px inset rgba(0, 0, 0, 0.15)
 * 3. Multi-anchor chromatic specular bloom (radiant ice-cyan/white apex reflection)
 * 4. Hairline beveled refraction stroke with edge-bulge optical perception
 */
fun Modifier.shuDingLiquidGlass(
    shape: Shape = PillShape,
    isDark: Boolean = true,
    accentGlow: Color? = null,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .drawWithCache {
        val w = size.width
        val h = size.height

        // Layer 1: 140deg refraction gradient
        val layer1Brush = Brush.linearGradient(
            colors = if (isDark) {
                listOf(
                    Color(0x28FFFFFF),
                    Color(0x0AFFFFFF),
                    Color(0x18FFFFFF),
                    Color(0x2AFFFFFF)
                )
            } else {
                listOf(
                    Color(0x95FFFFFF),
                    Color(0x50FFFFFF),
                    Color(0x70FFFFFF),
                    Color(0x90FFFFFF)
                )
            },
            start = Offset(0f, 0f),
            end = Offset(w, h * 0.85f)
        )

        // Layer 2: 180deg vertical depth gradient
        val layer2Brush = Brush.verticalGradient(
            colors = if (isDark) {
                listOf(Color(0xD0182434), Color(0xEA0D1520))
            } else {
                listOf(Color(0xF5FFFFFF), Color(0xE5F1F5F9))
            }
        )

        // Inset bevel shadow: simulates box-shadow: 0 -10px 25px inset rgba(0,0,0,0.2)
        val insetShadowBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                if (isDark) Color(0x35000000) else Color(0x12000000)
            )
        )

        // Top specular glint
        val glintBrush = Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                if (isDark) Color(0x90FFFFFF) else Color(0xC0FFFFFF),
                if (isDark) Color(0xC5BAF4FF) else Color(0xE0BAF4FF),
                if (isDark) Color(0x90FFFFFF) else Color(0xC0FFFFFF),
                Color.Transparent
            )
        )

        onDrawWithContent {
            // 1. Draw base shell depth
            drawRect(layer2Brush)
            // 2. Draw 140deg refraction layer
            drawRect(layer1Brush)
            // 3. Draw inset bottom bevel shadow
            drawRect(insetShadowBrush)

            // 4. Multi-anchor bloom if accent provided
            if (accentGlow != null) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentGlow.copy(alpha = if (isDark) 0.35f else 0.22f),
                            accentGlow.copy(alpha = if (isDark) 0.12f else 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.35f),
                        radius = w * 0.6f
                    )
                )
            }

            // 5. Draw child content
            drawContent()

            // 6. Top apex specular glint
            drawRect(
                brush = glintBrush,
                topLeft = Offset(w * 0.12f, 0f),
                size = Size(w * 0.76f, 1.5f)
            )
        }
    }
    .border(borderWidth, ShuDingSpecularRimBrush, shape)

