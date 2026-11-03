package com.expensetracker.app.core.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MaterialDarkColorScheme = darkColorScheme(
    primary = DarkAccentPrimary,
    onPrimary = DarkCanvas,
    primaryContainer = DarkSurfaceRaised,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkIncome,
    onSecondary = DarkCanvas,
    error = DarkExpense,
    onError = DarkCanvas,
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceRaised,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkDivider
)

private val MaterialLightColorScheme = lightColorScheme(
    primary = LightAccentPrimary,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceRaised,
    onPrimaryContainer = LightTextPrimary,
    secondary = LightIncome,
    onSecondary = LightSurface,
    error = LightExpense,
    onError = LightSurface,
    background = LightCanvas,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceRaised,
    onSurfaceVariant = LightTextSecondary,
    outline = LightDivider
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val materialScheme = if (darkTheme) MaterialDarkColorScheme else MaterialLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = palette.canvas.toArgb()
            window.navigationBarColor = palette.canvas.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAppPalette provides palette
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

object AppTheme {
    val colors: AppPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAppPalette.current

    val typography: androidx.compose.material3.Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: androidx.compose.material3.Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val spacing: AppSpacing
        get() = AppSpacing
}

