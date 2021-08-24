package com.wbrawner.budget.ui.base

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
        content = content
    )
}

@Composable
fun TwigsApp(darkMode: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    TwigsTheme(darkMode = darkMode) {
        Surface(content = content)
    }
}