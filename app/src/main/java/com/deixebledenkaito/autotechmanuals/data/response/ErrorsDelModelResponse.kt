package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.ErrorsDelModel

data class ErrorsDelModelResponse(
    val id: String = "",
    val numero: String = "",
    val descripcio: String = ""
) {
    // Converteix la resposta a la classe de domini
    fun toDomain(): ErrorsDelModel {
        return ErrorsDelModel(
            id = "", // L'ID no es guarda a Firestore, es pot deixar buit
            numero = this.numero,
            descripcio = this.descripcio
        )
    }
}