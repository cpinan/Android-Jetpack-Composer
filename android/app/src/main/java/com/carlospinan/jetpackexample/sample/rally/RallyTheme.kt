package com.carlospinan.jetpackexample.sample.rally

import androidx.compose.Composable
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.MaterialTypography
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight

val rallyGreen = Color(0xFF1EB980)
val rallyDarkGreen = Color(0xFF045D56)
val rallyOrange = Color(0xFFFF6859)
val rallyYellow = Color(0xFFFFCF44)
val rallyPurple = Color(0xFFB15DFF)
val rallyBlue = Color(0xFF72DEFF)

@Composable
fun RallyTheme(children: @Composable() () -> Unit) {
    val colors = MaterialColors(
        primary = rallyGreen,
        primaryVariant = rallyDarkGreen,
        secondary = rallyBlue,
        surface = Color(0xFF33333D),
        onSurface = Color.White,
        background = Color(0xFF26282F),
        onBackground = Color.White
    )
    val typography = MaterialTypography(
        h1 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w100,
            fontSize = 96.sp
        ),
        h2 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w100,
            fontSize = 60.sp
        ),
        h3 = TextStyle(
            fontFamily = FontFamily("Eczar"),
            fontWeight = FontWeight.w500,
            fontSize = 48.sp
        ),
        h4 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w700,
            fontSize = 34.sp
        ),
        h5 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w700,
            fontSize = 24.sp
        ),
        h6 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w700,
            fontSize = 20.sp
        ),
        subtitle1 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w700,
            fontSize = 16.sp
        ),
        subtitle2 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w500,
            fontSize = 14.sp
        ),
        body1 = TextStyle(
            fontFamily = FontFamily("Eczar"),
            fontWeight = FontWeight.w700,
            fontSize = 16.sp
        ),
        body2 = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w200,
            fontSize = 14.sp
        ),
        button = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w800,
            fontSize = 14.sp
        ),
        caption = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w500,
            fontSize = 12.sp
        ),
        overline = TextStyle(
            fontFamily = FontFamily("RobotoCondensed"),
            fontWeight = FontWeight.w500,
            fontSize = 10.sp
        )

    )
    MaterialTheme(colors = colors, typography = typography) {
        children()
    }
}