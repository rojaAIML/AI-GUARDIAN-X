package com.example.aiguardianx.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.Typography

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF8CF4FF),
    secondary = CyberEmerald,
    onSecondary = Color(0xFF003919),
    secondaryContainer = Color(0xFF005327),
    onSecondaryContainer = Color(0xFF69FF9E),
    tertiary = CyberViolet,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark,
    error = CyberCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyberCyanLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7F4FC),
    onPrimaryContainer = Color(0xFF001F26),
    secondary = Color(0xFF008947),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB4F9C6),
    onSecondaryContainer = Color(0xFF00210B),
    tertiary = Color(0xFF5B38CC),
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = CardBorderLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun AIGuardianXTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
