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

private val SteplyLightColorScheme = lightColorScheme(
    primary = Color(0xFF2F6F62),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7ECE5),
    onPrimaryContainer = Color(0xFF083A31),
    secondary = Color(0xFF496A9A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE7F7),
    onSecondaryContainer = Color(0xFF102A49),
    tertiary = Color(0xFF8A5B18),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE2B6),
    onTertiaryContainer = Color(0xFF3E2800),
    background = Color(0xFFF7F9F6),
    onBackground = Color(0xFF17211D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF17211D),
    surfaceVariant = Color(0xFFE3EAE6),
    onSurfaceVariant = Color(0xFF4F5F58),
    outline = Color(0xFFB7C2BC),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
)

private val SteplyDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA8D8CA),
    onPrimary = Color(0xFF07372F),
    primaryContainer = Color(0xFF1A554B),
    onPrimaryContainer = Color(0xFFD7ECE5),
    secondary = Color(0xFFB7C9E4),
    onSecondary = Color(0xFF1B314F),
    secondaryContainer = Color(0xFF314C73),
    onSecondaryContainer = Color(0xFFDCE7F7),
    tertiary = Color(0xFFEAC286),
    onTertiary = Color(0xFF4B3102),
    tertiaryContainer = Color(0xFF6D4710),
    onTertiaryContainer = Color(0xFFFFE2B6),
    background = Color(0xFF111916),
    onBackground = Color(0xFFE4ECE7),
    surface = Color(0xFF18211D),
    onSurface = Color(0xFFE4ECE7),
    surfaceVariant = Color(0xFF3E4A45),
    onSurfaceVariant = Color(0xFFC3CCC6),
    outline = Color(0xFF8D9993),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
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
