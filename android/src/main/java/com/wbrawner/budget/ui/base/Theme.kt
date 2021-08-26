package com.wbrawner.budget.ui.base

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.wbrawner.budget.R

val lightColors = lightColors(
    primary = Green500,
    primaryVariant = Green700,
)

val darkColors = darkColors(
    primary = Green300,
    primaryVariant = Green500,
    background = Color.Black,
    surface = Color.Black,
)

@Composable
fun TwigsTheme(darkMode: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkMode) darkColors else lightColors,
        typography = MaterialTheme.typography.copy(
            h1 = MaterialTheme.typography.h1.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            h2 = MaterialTheme.typography.h2.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            h3 = MaterialTheme.typography.h3.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            h4 = MaterialTheme.typography.h4.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            h5 = MaterialTheme.typography.h5.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            h6 = MaterialTheme.typography.h6.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            subtitle1 = MaterialTheme.typography.subtitle1.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            subtitle2 = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            body1 = MaterialTheme.typography.body1.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            body2 = MaterialTheme.typography.body2.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            button = MaterialTheme.typography.button.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            caption = MaterialTheme.typography.caption.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
            overline = MaterialTheme.typography.overline.copy(fontFamily = FontFamily(Font(R.font.ubuntu))),
        ),
        content = content
    )
}

@Composable
fun TwigsApp(darkMode: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    TwigsTheme(darkMode = darkMode) {
        Surface(content = content)
    }
}