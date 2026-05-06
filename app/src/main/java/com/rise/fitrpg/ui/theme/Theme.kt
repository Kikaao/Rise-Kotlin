package com.rise.fitrpg.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RiseDarkColorScheme = darkColorScheme(
    primary      = PurplePrimary,
    onPrimary    = TextPrimary,
    secondary    = PurpleLight,
    background   = BackgroundDark,
    surface      = SurfaceDark,
    onBackground = TextPrimary,
    onSurface    = TextPrimary,
    outline      = BorderColor
)

@Composable
fun RiseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RiseDarkColorScheme,
        typography  = RiseTypography,
        content     = content
    )
}