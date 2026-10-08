package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DhanRatanColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = SlateDarkText,
    primaryContainer = CardSurfaceElevated,
    onPrimaryContainer = GoldLight,
    secondary = EmeraldPrimary,
    onSecondary = SlateDarkText,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = EmeraldLight,
    tertiary = SkyPrimary,
    onTertiary = SlateDarkText,
    background = ObsidianBg,
    onBackground = SlateTextPrimary,
    surface = CardSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = CardSurfaceElevated,
    onSurfaceVariant = SlateTextMuted,
    error = RosePrimary,
    onError = SlateTextPrimary
)

@Composable
fun DhanRatanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DhanRatanColorScheme,
        typography = Typography,
        content = content
    )
}
