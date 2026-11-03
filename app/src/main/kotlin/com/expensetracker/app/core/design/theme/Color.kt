package com.expensetracker.app.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Dark Palette Tokens
val DarkCanvas = Color(0xFF0B0E14)
val DarkSurface = Color(0xFF141A24)
val DarkSurfaceRaised = Color(0xFF1C2431)
val DarkTextPrimary = Color(0xFFF4F6FC)
val DarkTextSecondary = Color(0xFFAAB4C4)
val DarkAccentPrimary = Color(0xFF9B8CFF)
val DarkIncome = Color(0xFF55D6A2)
val DarkExpense = Color(0xFFFF7D8A)
val DarkDivider = Color(0xFF293241)
val DarkWarning = Color(0xFFFFB020)
val DarkGlassBackground = Color(0xD9101724) // 85% opacity
val DarkGlassBorder = Color(0x33FFFFFF)      // 20% white stroke

// Light Palette Tokens
val LightCanvas = Color(0xFFF5F7FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceRaised = Color(0xFFEEF1F7)
val LightTextPrimary = Color(0xFF171C27)
val LightTextSecondary = Color(0xFF556174)
val LightAccentPrimary = Color(0xFF5946D8)
val LightIncome = Color(0xFF147A55)
val LightExpense = Color(0xFFB7354B)
val LightDivider = Color(0xFFDDE3ED)
val LightWarning = Color(0xFFD97706)
val LightGlassBackground = Color(0xECFFFFFF) // ~92% opacity
val LightGlassBorder = Color(0x24000000)     // 14% black stroke

@Immutable
data class AppPalette(
    val canvas: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentPrimary: Color,
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
    textTertiary = Color(0xFF6F7B8F),
    accentPrimary = DarkAccentPrimary,
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
    textTertiary = Color(0xFF8895A7),
    accentPrimary = LightAccentPrimary,
    income = LightIncome,
    expense = LightExpense,
    divider = LightDivider,
    warning = LightWarning,
    glassBackground = LightGlassBackground,
    glassBorder = LightGlassBorder,
    isDark = false
)

val LocalAppPalette = staticCompositionLocalOf { DarkPalette }

