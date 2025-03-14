package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.ChangeNavigationBarColor


import android.os.Build
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

