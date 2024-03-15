package com.wbrawner.twigs.android.ui.base

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.wbrawner.twigs.android.R

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
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
        colorScheme = if (darkMode) DarkColors else LightColors,
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