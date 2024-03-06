package com.preachooda.assets.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = SecondaryRed,
    tertiary = Pink40,

    /* Other default colors to override */
    background = PrimaryWhite,
    surface = SecondaryWhite,
    onPrimary = PrimaryWhite,
    onSecondary = PrimaryWhite,
    onTertiary = PrimaryWhite,
    onBackground = PrimaryBlack,
    onSurface = PrimaryBlack,
    /**/
)

@Composable
fun BokuNoHeroServiceTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
