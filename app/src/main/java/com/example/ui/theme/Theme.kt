package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NovaCyan,
    onPrimary = NovaDarkBg,
    primaryContainer = NovaCyanGlow,
    onPrimaryContainer = NovaCyan,
    secondary = NovaPink,
    onSecondary = NovaTextPrimary,
    secondaryContainer = NovaPinkGlow,
    onSecondaryContainer = NovaPink,
    tertiary = NovaPurple,
    background = NovaDarkBg,
    onBackground = NovaTextPrimary,
    surface = NovaPanelBg,
    onSurface = NovaTextPrimary,
    surfaceVariant = NovaPanelAlt,
    onSurfaceVariant = NovaTextSecondary,
    outline = NovaBorder,
    error = NovaPink
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = NovaDarkBg.toArgb()
                window.navigationBarColor = NovaDarkBg.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
