package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model

// Resposta de Firestore per a un manual
data class ModelResponse(
    val id: String ="",
    val nom: String="",
    val descripcio: String="",
    val imageUrl: String=""
) {
    fun toDomainModel(): Model {
        return Model(
            id= id,
            nom = nom,
            descripcio = descripcio,
            imageUrl = imageUrl
        )
    }
}