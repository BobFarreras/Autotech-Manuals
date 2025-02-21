package com.deixebledenkaito.autotechmanuals.data.response


import com.deixebledenkaito.autotechmanuals.domain.Manuals

// Resposta de Firestore per a un manual
data class ManualResponse(
    var id: String ="",
    var nom: String="",
    var descripcio: String="",
    var imageUrl: String="",
    var usageCount: Long = 0

) {
    fun toDomain(): Manuals{
        return Manuals(
            id = id,
            nom = nom,
            descripcio = descripcio,
            imageUrl = imageUrl,
            usageCount = usageCount

        )
    }
}

