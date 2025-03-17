package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.ImageVideo

data class AportacioResponse(
    val id: String,
    val model: String,
    val manual: String,
    val title: String,
    val descripcio: String,
    val imageVideos: List<ImageVideo> = emptyList(), // Llista d'imatges i vídeos
    val likes: Int,
    val noLikes: Int,
    val usageCount: Long,
    val pdfUrls: String?, // URL del PDF (opcional)
    val data: String, // Nou camp per a la data
    val hora: String, // Nou camp per a l'hora
    val user: String,
    val userName: String,
    val usersWhoLiked: MutableList<String> = mutableListOf(), // Canvia a MutableList
    val usersWhoDisliked: MutableList<String> = mutableListOf() // Canvia a MutableList
) {
    fun toDomain(): AportacioUser {
        return AportacioUser(
            id = id,
            model = model,
            manual = manual,
            title = title,
            descripcio = descripcio,
            imageVideos = imageVideos, // Nou camp per a imatges i vídeos
            likes = likes,
            noLikes = noLikes,
            usageCount = usageCount,
            pdfUrls = pdfUrls,
            data = data,
            hora = hora,
            user = user,
            userName = userName,
            usersWhoLiked = usersWhoLiked,
            usersWhoDisliked = usersWhoDisliked
        )
    }
}