package com.expensetracker.app.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Dark Palette Tokens - Deep Obsidian Glass Aesthetic
val DarkCanvas = Color(0xFF090A0F)
val DarkSurface = Color(0xFF131620)
val DarkSurfaceRaised = Color(0xFF1B1F2D)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextTertiary = Color(0xFF64748B)
val DarkAccentPrimary = Color(0xFF6366F1) // Electric Royal Indigo
val DarkVioletHighlight = Color(0xFF8B5CF6) // Restrained Violet Highlight
val DarkIncome = Color(0xFF10B981)        // Fresh Mint Emerald
val DarkExpense = Color(0xFFF43F5E)       // Vibrant Sunset Coral
val DarkDivider = Color(0xFF1E2433)
val DarkWarning = Color(0xFFF59E0B)
val DarkGlassBackground = Color(0x1F141A28)
val DarkGlassBorder = Color(0x35FFFFFF)

// Light Palette Tokens - Clean Porcelain Glass Aesthetic
val LightCanvas = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceRaised = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextTertiary = Color(0xFF94A3B8)
val LightAccentPrimary = Color(0xFF4F46E5)
val LightVioletHighlight = Color(0xFF7C3AED)
val LightIncome = Color(0xFF059669)
val LightExpense = Color(0xFFE11D48)
val LightDivider = Color(0xFFE2E8F0)
val LightWarning = Color(0xFFD97706)
val LightGlassBackground = Color(0xE8FFFFFF)
val LightGlassBorder = Color(0x20000000)

@Immutable
data class AppPalette(
    val canvas: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentPrimary: Color,
    val violetHighlight: Color = DarkVioletHighlight,
    val income: Color,
    val expense: Color,
    val divider: Color,
    val warning: Color,
    val glassBackground: Color,
    val glassBorder: Color,
    val isDark: Boolean
)

val DarkPalette = AppPalette(
    canvas = DarkCanvas,
    surface = DarkSurface,
    surfaceRaised = DarkSurfaceRaised,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    accentPrimary = DarkAccentPrimary,
    violetHighlight = DarkVioletHighlight,
    income = DarkIncome,
    expense = DarkExpense,
    divider = DarkDivider,
    warning = DarkWarning,
    glassBackground = DarkGlassBackground,
    glassBorder = DarkGlassBorder,
    isDark = true
)

val LightPalette = AppPalette(
    canvas = LightCanvas,
    surface = LightSurface,
    surfaceRaised = LightSurfaceRaised,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    accentPrimary = LightAccentPrimary,
    violetHighlight = LightVioletHighlight,
    income = LightIncome,
    expense = LightExpense,
    divider = LightDivider,
    warning = LightWarning,
    glassBackground = LightGlassBackground,
    glassBorder = LightGlassBorder,
    isDark = false
)

val LocalAppPalette = staticCompositionLocalOf { DarkPalette }
