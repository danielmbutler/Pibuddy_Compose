package com.example.piBuddyCompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = primary,
    primaryVariant = primary_dark,
    secondary = secondary,
    onSurface = secondary
)

private val LightColorPalette = lightColors(
    primary = primary,
    primaryVariant = primary_light,
    secondary = secondary,
    onSurface = secondary,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    ,
    */
)


@Composable
fun PibuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )

}

@Composable
fun pibuddyTextFieldColors() : TextFieldColors {
    //Text Field Colors
    val PibuddyTextFieldColors =   TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = secondary,
        unfocusedBorderColor = primary
    )

    return PibuddyTextFieldColors
}




