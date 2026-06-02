package com.bignerdranch.android.playlistmaker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    background = YPBlack,
    surface = YPLightGrey,
    primary = YPBlue,
    secondary = YPTextGrey,
    onBackground = YPWhite,
    onSurface = YPWhite,
)

private val LightColors = lightColorScheme(
    background = YPWhite,
    surface = YPLightGrey,
    primary = YPBlue,
    secondary = YPTextGrey,
    onBackground = YPBlack,
    onSurface = YPBlack
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}