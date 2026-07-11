package com.steply.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color scheme mirrors the Steply web dashboard design tokens
// (client/src/styles/tokens.css) so the phone and PC surfaces share one brand.
// `primary` uses the web's deeper teal (--primary-dark #087765) so filled
// buttons keep an accessible contrast ratio with white text, while the brand
// mint (--primary #18BFA6) lives in containers and accents.
private val SteplyLightColorScheme = lightColorScheme(
    primary = Color(0xFF087765),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF8F0),
    onPrimaryContainer = Color(0xFF05231D),
    secondary = Color(0xFF4F7F98),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9F5FF),
    onSecondaryContainer = Color(0xFF12313F),
    tertiary = Color(0xFF8A5F00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF6CF),
    onTertiaryContainer = Color(0xFF382A00),
    background = Color(0xFFF6FFFB),
    onBackground = Color(0xFF10241F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF10241F),
    surfaceVariant = Color(0xFFEEF8F4),
    onSurfaceVariant = Color(0xFF526A63),
    outline = Color(0xFFC6D8D2),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val SteplyDarkColorScheme = darkColorScheme(
    primary = Color(0xFF87E6D3),
    onPrimary = Color(0xFF05231D),
    primaryContainer = Color(0xFF174A41),
    onPrimaryContainer = Color(0xFFA9F3E4),
    secondary = Color(0xFFB4D7EA),
    onSecondary = Color(0xFF17313F),
    secondaryContainer = Color(0xFF233F4F),
    onSecondaryContainer = Color(0xFFCFE6F5),
    tertiary = Color(0xFFF5D978),
    onTertiary = Color(0xFF3F3400),
    tertiaryContainer = Color(0xFF4E4210),
    onTertiaryContainer = Color(0xFFFFF6CF),
    background = Color(0xFF0D1714),
    onBackground = Color(0xFFEAF7F2),
    surface = Color(0xFF18231F),
    onSurface = Color(0xFFEAF7F2),
    surfaceVariant = Color(0xFF263B35),
    onSurfaceVariant = Color(0xFFBFD0CA),
    outline = Color(0xFF61756E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val SteplyTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

private val SteplyShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

@Composable
fun SteplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SteplyDarkColorScheme else SteplyLightColorScheme,
        typography = SteplyTypography,
        shapes = SteplyShapes,
        content = content,
    )
}
