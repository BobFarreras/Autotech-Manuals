package com.deixebledenkaito.autotechmanuals.data.network.firebstore

import android.content.Context


import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Opcional: Si vols que sigui una instància única
class SharedPreferencesHelper @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun saveLastUsedManual(manualName: String) {
        sharedPreferences.edit().putString("lastUsedManual", manualName).apply()
    }

    fun getLastUsedManual(): String? {
        return sharedPreferences.getString("lastUsedManual", null)
    }
}