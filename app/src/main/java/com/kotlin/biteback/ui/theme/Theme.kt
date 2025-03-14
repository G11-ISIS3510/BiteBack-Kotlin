package com.kotlin.biteback.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp

// Market Colors
val GreenDark = Color(0xFF386641)
val GreenMedium = Color(0xFF6A994E)
val GreenLight = Color(0xFFA7C957)
val BeigeLight = Color(0xFFF2E8CF)
val GreenAccent = Color(0xFF2DC653)
val RedAccent = Color(0xFFBC4749)

// Restaurant Colors
val NavyDark = Color(0xFF003049)
val RedPrimary = Color(0xFFD62828)
val OrangePrimary = Color(0xFFF77F00)
val YellowAccent = Color(0xFFFCBF49)
val BeigeSecondary = Color(0xFFEAE2B7)
val NavySecondary = Color(0xFF03071E)

private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    secondary = OrangePrimary,
    background = BeigeLight,
    surface = Color.White,
    onBackground = NavyDark,
    onSurface = BeigeLight,
)

private val DarkColors = darkColorScheme(
    primary = GreenLight,
    onPrimary = Color.Black,
    secondary = YellowAccent,
    background = NavySecondary,
    surface = NavyDark,
    onBackground = BeigeSecondary,
    onSurface = BeigeLight
)


@OptIn(ExperimentalTextApi::class)
val NunitoSans = FontFamily(
    Font(
        resId = com.kotlin.biteback.R.font.nunito_sans_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.Setting("wght", 400f) // Normal
        ) // Normal // Normal
    ),
    Font(
        resId = com.kotlin.biteback.R.font.nunito_sans_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.Setting("wght", 700f) // Bold
        ) // Bold
    ),
    Font(
        resId = com.kotlin.biteback.R.font.nunito_sans_italic_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.Setting("wght", 400f) // Italic Normal
        ), // Italic Normal
        style = androidx.compose.ui.text.font.FontStyle.Italic
    )
)

val NunitoTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
)


@Composable
fun BiteBackTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = NunitoTypography,
        content = content
    )
}