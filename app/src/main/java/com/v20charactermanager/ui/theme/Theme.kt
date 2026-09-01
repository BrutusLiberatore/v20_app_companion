package com.v20charactermanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val V20GreenDarkColorScheme = darkColorScheme(
    primary = V20GreenBright,
    onPrimary = V20GreenDark,
    primaryContainer = V20Green,
    onPrimaryContainer = Color(0xFFF0F0F0),
    secondary = V20Gold,
    onSecondary = V20Black,
    secondaryContainer = V20GoldDark,
    onSecondaryContainer = V20GoldBright,
    tertiary = V20GreenLight,
    onTertiary = V20Black,
    background = V20Black,
    onBackground = V20Ink,
    surface = V20Surface,
    onSurface = V20Ink,
    surfaceVariant = V20Surface2,
    onSurfaceVariant = V20InkDim,
    outline = V20Line,
    error = V20ErrorBright,
    onError = Color(0xFFF0F0F0)
)

@Composable
fun V20Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = V20GreenDarkColorScheme,
        typography = V20Typography,
        content = content
    )
}
