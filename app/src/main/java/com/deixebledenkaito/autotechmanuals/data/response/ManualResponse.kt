package com.deixebledenkaito.autotechmanuals.data.response


import com.deixebledenkaito.autotechmanuals.domain.Manuals

// Resposta de Firestore per a un manual
data class ManualResponse(
    val id: String ="",
    val nom: String="",
    val descripcio: String="",
    val imageUrl: String=""
) {
    fun toDomain(): Manuals{
        return Manuals(
            id= id,
            nom = nom,
            descripcio = descripcio,
            imageUrl = imageUrl
        )
    }
}

