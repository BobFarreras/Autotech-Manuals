package com.deixebledenkaito.autotechmanuals.domain

import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.ui.home.getImageResIdFromManualName

// Model de dades per a un manual
data class Manuals(
    val id: String = "",
    val nom: String= "",
    val descripcio: String= "",
    val imageResId: Int = R.drawable.ic_gallery, // Imatge local
    val usageCount: Long= 0

)
