package com.fernando.jarvis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisColors.Neon,
    secondary = JarvisColors.Purple,
    tertiary = JarvisColors.Magenta,
    background = JarvisColors.Deep,
    surface = JarvisColors.Card,
    surfaceVariant = JarvisColors.Space,
    onPrimary = JarvisColors.Deep,
    onSecondary = JarvisColors.Deep,
    onBackground = JarvisColors.TextPrimary,
    onSurface = JarvisColors.TextPrimary,
    error = JarvisColors.Red,
    outline = JarvisColors.CardBorder,
)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        content = content,
    )
}
