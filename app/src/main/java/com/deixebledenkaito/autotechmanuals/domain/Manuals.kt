package com.deixebledenkaito.autotechmanuals.domain

// Model de dades per a un manual
data class Manuals(
    val id: String,
    val nom: String,
    val descripcio: String,
    val imageUrl: String
)
