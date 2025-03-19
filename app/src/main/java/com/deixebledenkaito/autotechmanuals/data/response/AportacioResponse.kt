package com.deixebledenkaito.autotechmanuals.data.response

import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.ImageVideo

data class AportacioResponse(
    val id: String,
    val model: String,
    val manual: String,
    val title: String,
    val descripcio: String,
    val imatgesUrl: List<String> = emptyList(), // Llista d'URLs d'imatges
    val videosUrl: List<String> = emptyList(), // Llista d'URLs de vídeos
    val miniaturesUrl: List<String> = emptyList(), // Llista d'URLs de miniatures
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
            imatgesUrl = imatgesUrl,
            videosUrl = videosUrl,
            miniaturesUrl = miniaturesUrl,
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