package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.AportacioUser

data class AportacioResponse(
    val id: String,
    val model: String,
    val manual: String,
    val title: String,
    val descripcio: String,
    val imageUrls: List<String>,
    val stars: Int,
    val usageCount: Long,
    val pdfUrls: String?, // URL del PDF (opcional)
    val videoUrls: String?, // Nou camp per a l'URL del vídeo (opcional)
    val data: String, // Nou camp per a la data
    val hora: String ,// Nou camp per a l'hora
    val user:String
) {
    fun toDomain(): AportacioUser {
        return AportacioUser(
            id = id,
            model = model,
            manual = manual,
            title = title,
            descripcio = descripcio,
            imageUrls = imageUrls.toString(),
            stars = stars,
            usageCount = usageCount,
            pdfUrls = pdfUrls,
            videoUrls = videoUrls, // Nou camp per al vídeo
            data = data,
            hora = hora,
            user = user
        )
    }
}