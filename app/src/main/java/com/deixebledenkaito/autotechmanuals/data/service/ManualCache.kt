package com.deixebledenkaito.autotechmanuals.data.service

import com.deixebledenkaito.autotechmanuals.domain.Manuals

class ManualCache {

    private val topManualsCache = mutableListOf<String>()
    private val manualsCache = mutableMapOf<String, Manuals>()

    fun getManualByName(manualName: String): Manuals? {
        return manualsCache[manualName]
    }

    fun saveManual(manual: Manuals) {
        manualsCache[manual.nom] = manual
    }

    fun getTopManuals(): List<String>? {
        return if (topManualsCache.isNotEmpty()) topManualsCache else null
    }

    fun saveTopManuals(topManuals: List<String>) {
        topManualsCache.clear()
        topManualsCache.addAll(topManuals)
    }
}