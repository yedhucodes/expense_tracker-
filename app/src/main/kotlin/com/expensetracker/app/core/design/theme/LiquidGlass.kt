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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

val LocalHazeState = compositionLocalOf { HazeState() }

/**
 * Atmospheric background with elegant celestial ambient aura:
 * Soft Electric Indigo at the top and oceanic sapphire depth bloom.
 * Free of muddy green or clashing rainbow blobs for a calm, ultra-luxurious feel.
 */
@Composable
fun AtmosphericBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = AppTheme.colors.isDark
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.canvas)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (isDark) {
                // Top luminous Indigo atmospheric ambient aura (cohesive with royal indigo accent)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x356366F1), // Electric Indigo soft bloom
                            Color(0x184F46E5),
                            Color(0x081E1B4B),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.50f, 0f),
                        radius = (w * 0.95f) * pulse
                    )
                )

                // Mid-depth oceanic sapphire ambient glow (smooth, matching lighting)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1838BDF8),
                            Color(0x060284C7),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f, h * 0.38f),
                        radius = w * 0.70f
                    )
                )

                // Soft lower violet illumination
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x14818CF8),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.15f, h * 0.75f),
                        radius = (w * 0.75f) * (2f - pulse)
                    )
                )
            } else {
                // Light mode: ethereal soft sky & lavender ambient pastels
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x25C7D2FE), Color(0x0CE0E7FF), Color.Transparent),
                        center = Offset(w * 0.50f, 0f),
                        radius = (w * 0.85f) * pulse
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x20BAE6FD), Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.35f),
                        radius = (w * 0.65f) * (2f - pulse)
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
    tint: Color = if (AppTheme.colors.isDark) Color(0xD0111522) else Color(0xEBFFFFFF),
    blurRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    borderTopColor: Color = if (AppTheme.colors.isDark) Color(0x50FFFFFF) else Color(0x99FFFFFF),
    borderBottomColor: Color = if (AppTheme.colors.isDark) Color(0x12FFFFFF) else Color(0x1A000000)
): Modifier {
    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                backgroundColor = if (AppTheme.colors.isDark) Color(0xFF090A0F) else Color(0xFFF8FAFC),
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
 * Renders a crystalline glass surface with semi-translucent gradient tint and
 * a 1.dp hairline metallic reflection gradient stroke over the atmospheric canvas.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = if (AppTheme.colors.isDark) Color(0xD0111522) else Color(0xEBFFFFFF),
    borderWidth: Dp = 1.dp,
    borderTopColor: Color = if (AppTheme.colors.isDark) Color(0x50FFFFFF) else Color(0x99FFFFFF),
    borderBottomColor: Color = if (AppTheme.colors.isDark) Color(0x12FFFFFF) else Color(0x1A000000),
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
            colors = listOf(Color(0x2E161C2A), Color(0x1A0F1420))
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
                            if (isDark) Color(0x40FFFFFF) else Color(0x80FFFFFF),
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
 * Derived from physical light reflection with pure diamond specular highlight.
 */
val ShuDingSpecularRimBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xA0FFFFFF), // 0.62 specular white
        Color(0x40FFFFFF), // 0.25 clear rim
        Color(0x10FFFFFF), // 0.06 hairline highlight
        Color(0x30FFFFFF)  // 0.19 bottom ambient reflection
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

/**
 * Refractive Rim Brush for Light Theme:
 * Provides subtle edge definition and contrast on light canvases.
 */
val ShuDingLightRimBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x70FFFFFF), // Top specular reflection
        Color(0x25000000), // Lateral refractive edge
        Color(0x18000000), // Soft ambient shadow
        Color(0x28000000)  // Bottom defining bevel
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

/**
 * Modifier applying Shu Ding's physical Liquid Glass optical model:
 * 1. Dual-layer shell gradients (140deg refraction angle + 180deg depth angle)
 * 2. Inset bottom bevel shadow: simulates box-shadow: 0 -10px 25px inset rgba(0, 0, 0, 0.15)
 * 3. Multi-anchor chromatic specular bloom (luminous accent reflection)
 * 4. Hairline beveled refraction stroke with pure diamond specular rim
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
                    Color(0x22FFFFFF),
                    Color(0x08FFFFFF),
                    Color(0x14FFFFFF),
                    Color(0x25FFFFFF)
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
                listOf(Color(0xCC111420), Color(0xE50B0E17))
            } else {
                listOf(Color(0xF8FFFFFF), Color(0xEEF1F5F9))
            }
        )

        // Inset bevel shadow: simulates box-shadow: 0 -10px 25px inset rgba(0,0,0,0.2)
        val insetShadowBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                if (isDark) Color(0x30000000) else Color(0x18000000)
            )
        )

        // Top specular glint
        val glintBrush = Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                if (isDark) Color(0x80FFFFFF) else Color(0x90FFFFFF),
                if (isDark) Color(0xC0FFFFFF) else Color(0xD0FFFFFF),
                if (isDark) Color(0x80FFFFFF) else Color(0x90FFFFFF),
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
                            accentGlow.copy(alpha = if (isDark) 0.38f else 0.32f),
                            accentGlow.copy(alpha = if (isDark) 0.14f else 0.12f),
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
    .border(borderWidth, if (isDark) ShuDingSpecularRimBrush else ShuDingLightRimBrush, shape)
