package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.ChangeNavigationBarColor

import android.app.Activity
import android.os.Build



import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import android.content.Context


fun isDarkTheme(context: Context): Boolean {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            val configuration = context.resources.configuration
            (configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
        else -> false
    }
}

@Composable
fun ChangeNavigationBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) { // Assegura que no s'executi en mode de previsualització
        val window = (view.context as Activity).window
        val context = view.context
        SideEffect {
            window.navigationBarColor = color.toArgb() // Canvia el color de la barra de navegació
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme(context) // Ajusta el color dels icons
        }
    }
}