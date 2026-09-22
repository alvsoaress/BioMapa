package org.example.biomapa

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BioMapaLightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFF558B2F),
    surface = Color(0xFFF1F8E9),
    onSurface = Color(0xFF1B381B),
    error = Color(0xFFB3261E)
)

private val BioMapaDarkColors = darkColorScheme(
    primary = Color(0xFF81C784),
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFAED581),
    surface = Color(0xFF121E12),
    onSurface = Color(0xFFE8F5E9),
    error = Color(0xFFF2B8B5)
)

@Composable
fun BioMapaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) BioMapaDarkColors else BioMapaLightColors

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}