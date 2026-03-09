package com.meta_force.meta_force.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MF_Teal,
    onPrimary = MF_BlueDeep,
    secondary = MF_BlueLight,
    onSecondary = MF_Teal,
    tertiary = MF_Slate,
    background = MF_BlueDeep,
    onBackground = MF_White,
    surface = MF_BlueLight,
    onSurface = MF_White,
    error = MF_Red,
    onError = MF_White
)

// We want a dark aesthetic by default, so we might reuse dark scheme or define a similar light one
private val LightColorScheme = lightColorScheme(
    primary = MF_Teal,
    onPrimary = MF_BlueDeep,
    secondary = MF_BlueLight,
    onSecondary = MF_Teal,
    tertiary = MF_Slate,
    background = MF_BlueDeep, // Force Dark bg even in light mode for consistency
    onBackground = MF_White,
    surface = MF_BlueLight,
    onSurface = MF_White,
    error = MF_Red,
    onError = MF_White
)

@Composable
fun Meta_forceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamic color to enforce our brand identity
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}