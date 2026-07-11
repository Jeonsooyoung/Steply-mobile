package com.steply.app.ui.screens.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Brand constants aligned with the Steply web design tokens
// (client/src/styles/tokens.css) for parity between phone and PC surfaces.
val SteplyBrandTeal = Color(0xFF18BFA6)
val SteplyDeepTeal = Color(0xFF087765)
val SteplyAccentLime = Color(0xFFC8DC3E)
val SteplyWarmAmber = Color(0xFFA06F00)
val SteplySuccessGreen = Color(0xFF13A88E)
val SteplySoftBlue = Color(0xFF4F7F98)
val SteplyWarmCream = Color(0xFFF6FFFB)

object SteplySpacing {
    val ExtraSmallGap = 4.dp
    val ScreenHorizontal = 20.dp
    val ScreenVertical = 16.dp
    val TopBarHorizontal = 16.dp
    val TopBarVertical = 8.dp
    val CardPadding = 18.dp
    val NoticePadding = 14.dp
    val SmallGap = 8.dp
    val MediumGap = 12.dp
    val CardGap = 12.dp
    val SectionGap = 16.dp
    val ChipHorizontal = 12.dp
    val ChipVertical = 6.dp
}

// Radii mirror the web tokens: --radius-card 8, --radius-button 16,
// --radius-input 12, --radius-notice 8.
object SteplyCorners {
    val Card = 8.dp
    val Button = 16.dp
    val Field = 12.dp
    val Notice = 8.dp
}

object SteplySizes {
    val ScreenMaxWidth = 640.dp
    val TopBarButton = 48.dp
    val IconSmall = 20.dp
    val IconMedium = 24.dp
    val IconLarge = 30.dp
    val IconExtraLarge = 34.dp
    val ActionIcon = 26.dp
    val ActionIconContainer = 52.dp
    val EmptyIconContainer = 58.dp
    val Avatar = 56.dp
    val PrimaryButtonMinHeight = 56.dp
    val SecondaryButtonMinHeight = 52.dp
    val ButtonBorderWidth = 1.dp
    val TimerStroke = 16.dp
}

object SteplyElevation {
    val Card = 1.dp
    val PrimaryCard = 3.dp
}
