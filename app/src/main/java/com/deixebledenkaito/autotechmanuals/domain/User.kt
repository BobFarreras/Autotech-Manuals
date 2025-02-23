package com.deixebledenkaito.autotechmanuals.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val description: String?,
    val stars: Int // Nou camp per a les estrelles
)