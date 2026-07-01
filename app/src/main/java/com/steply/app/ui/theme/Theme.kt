package com.steply.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SteplyLightColorScheme = lightColorScheme(
    primary = Color(0xFF4F8A7B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDECE7),
    onPrimaryContainer = Color(0xFF1F2933),
    secondary = Color(0xFF86A8E7),
    onSecondary = Color(0xFF1F2933),
    secondaryContainer = Color(0xFFE7EEFB),
    onSecondaryContainer = Color(0xFF1F2933),
    tertiary = Color(0xFFF4A261),
    onTertiary = Color(0xFF1F2933),
    tertiaryContainer = Color(0xFFFFE7D1),
    onTertiaryContainer = Color(0xFF1F2933),
    background = Color(0xFFF8F5EF),
    onBackground = Color(0xFF1F2933),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2933),
    surfaceVariant = Color(0xFFEFE8DD),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFD7CFC3),
    error = Color(0xFFD9534F),
    onError = Color.White,
    errorContainer = Color(0xFFFFE3E1),
    onErrorContainer = Color(0xFF7B1E1B),
)

private val SteplyTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
)

@Composable
fun SteplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SteplyLightColorScheme,
        typography = SteplyTypography,
        content = content,
    )
}
