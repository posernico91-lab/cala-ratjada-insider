package com.calaratjada.insider.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Emerald500,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald700,
    secondary = Stone600,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Stone100,
    onSecondaryContainer = Stone900,
    background = Stone50,
    onBackground = Stone900,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Stone900,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone600,
    outline = Stone300,
    error = Red500,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun CalaRatjadaInsiderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
