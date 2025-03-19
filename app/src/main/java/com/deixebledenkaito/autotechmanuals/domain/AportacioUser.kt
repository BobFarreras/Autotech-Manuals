package com.deixebledenkaito.autotechmanuals.domain

data class AportacioUser(
    val id: String = "",
    val model: String = "",
    val manual: String = "",
    val title: String = "",
    val descripcio: String = "",
    val imatgesUrl: List<String> = emptyList(), // Llista d'URLs d'imatges
    val videosUrl: List<String> = emptyList(), // Llista d'URLs de vídeos
    val miniaturesUrl: List<String> = emptyList(), // Llista d'URLs de miniatures
    var likes: Int = 0,
    var noLikes: Int = 0,
    val usageCount: Long = 0,
    val pdfUrls: String? = null,
    val data: String = "",
    val hora: String = "",
    val user: String = "",
    val userName: String = "",
    var usersWhoLiked: MutableList<String> = mutableListOf(),
    var usersWhoDisliked: MutableList<String> = mutableListOf()
) {
    fun toFirestore(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "model" to model,
            "manual" to manual,
            "title" to title,
            "descripcio" to descripcio,
            "imatgesUrl" to imatgesUrl,
            "videosUrl" to videosUrl,
            "miniaturesUrl" to miniaturesUrl,
            "likes" to likes,
            "noLikes" to noLikes,
            "usageCount" to usageCount,
            "pdfUrls" to (pdfUrls ?: ""),
            "data" to data,
            "hora" to hora,
            "user" to user,
            "userName" to userName,
            "usersWhoLiked" to usersWhoLiked,
            "usersWhoDisliked" to usersWhoDisliked
        )
    }
}

