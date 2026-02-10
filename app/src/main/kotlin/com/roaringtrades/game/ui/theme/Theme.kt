package com.roaringtrades.game.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = CharcoalBlack,
    primaryContainer = LightGold,
    onPrimaryContainer = CharcoalBlack,
    secondary = DeepBurgundy,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8C4C8),
    onSecondaryContainer = DarkBurgundy,
    tertiary = SteelBlue,
    onTertiary = Color.White,
    background = CreamWhite,
    onBackground = CharcoalBlack,
    surface = Color.White,
    onSurface = CharcoalBlack,
    surfaceVariant = SoftCream,
    onSurfaceVariant = Color(0xFF4A4A4A),
    error = DangerRed,
    onError = Color.White,
    outline = DarkGold
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = CharcoalBlack,
    primaryContainer = DarkGold,
    onPrimaryContainer = LightGold,
    secondary = Color(0xFFE8A0A8),
    onSecondary = DarkBurgundy,
    secondaryContainer = DeepBurgundy,
    onSecondaryContainer = Color(0xFFE8C4C8),
    tertiary = Color(0xFF8AB4D4),
    onTertiary = CharcoalBlack,
    background = CharcoalBlack,
    onBackground = CreamWhite,
    surface = Color(0xFF252540),
    onSurface = CreamWhite,
    surfaceVariant = Color(0xFF303050),
    onSurfaceVariant = Color(0xFFB8B8C8),
    error = Color(0xFFFF6B7A),
    onError = CharcoalBlack,
    outline = Gold
)

@Composable
fun RoaringTradesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
