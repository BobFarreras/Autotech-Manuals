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

