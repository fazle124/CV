package com.example.cv.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = SurfacePaper,
    primaryContainer = LightBlueAction,
    onPrimaryContainer = BluePrimaryDark,
    secondary = GreenAccent,
    onSecondary = SurfacePaper,
    background = BackgroundCanvas,
    onBackground = TextPrimary,
    surface = SurfacePaper,
    onSurface = TextPrimary,
    error = RedDanger
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = SurfacePaper,
    primaryContainer = BluePrimaryDark,
    onPrimaryContainer = LightBlueAction,
    secondary = GreenAccent,
    onSecondary = SurfacePaper,
    background = DarkBackground,
    onBackground = SurfacePaper,
    surface = DarkSurface,
    onSurface = SurfacePaper,
    error = RedDanger
)

@Composable
fun CvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
