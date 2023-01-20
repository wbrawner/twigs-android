package com.wbrawner.budget.ui.base

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.wbrawner.budget.R

val lightColors = lightColorScheme(
    primary = Green500,
    primaryContainer = Green300,
    secondary = Green700,
    secondaryContainer = Green300,
)

val darkColors = darkColorScheme(
    primary = Green300,
    primaryContainer = Green500,
    secondary = Green500,
    secondaryContainer = Green700,
    background = Color.Black,
    surface = Color.Black,
)

val ubuntu = FontFamily(
    Font(R.font.ubuntu_bold, weight = FontWeight.Bold),
    Font(R.font.ubuntu_regular, weight = FontWeight.Normal),
    Font(R.font.ubuntu_light, weight = FontWeight.Light),
    Font(R.font.ubuntu_bolditalic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.ubuntu_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.ubuntu_lightitalic, weight = FontWeight.Light, style = FontStyle.Italic),
)

@Composable
fun TwigsTheme(darkMode: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkMode) darkColors else lightColors,
        typography = MaterialTheme.typography.copy(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = ubuntu),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = ubuntu),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = ubuntu),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = ubuntu),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = ubuntu),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = ubuntu),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = ubuntu),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = ubuntu),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = ubuntu),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = ubuntu),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = ubuntu),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = ubuntu),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = ubuntu),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = ubuntu),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = ubuntu),
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