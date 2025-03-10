package com.deixebledenkaito.autotechmanuals.domain




data class AportacioUser(
    val id: String = "",
    val model: String = "",
    val manual: String = "",
    val title: String = "",
    val descripcio: String = "",
    val imageUrls: String = "",
    var likes: Int = 0,
    var noLikes: Int = 0,
    val usageCount: Long = 0,
    val pdfUrls: String? = null,
    val videoUrls: String? = null,
    val data: String = "",
    val hora: String = "",
    val user: String = "",
    val userName: String = "",
    var usersWhoLiked: MutableList<String> = mutableListOf(), // Canvia a MutableList
    var usersWhoDisliked: MutableList<String> = mutableListOf() // Canvia a MutableList
){


    fun toFirestore(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "model" to model,
            "manual" to manual,
            "title" to title,
            "descripcio" to descripcio,
            "imageUrls" to imageUrls, // Guardem totes les URLs de les imatges
            "likes" to likes,
            "noLikes" to noLikes,
            "usageCount" to usageCount,
            "pdfUrls" to (pdfUrls ?: ""), // Guarda una cadena buida si pdfUrl és null
            "videoUrls" to (videoUrls ?: ""), // Guarda una cadena buida si videoUrl és null
            "data" to data,
            "hora" to hora,
            "user" to user,
            "userName" to userName, // Afegim el nom de l'usuari
            "usersWhoLiked" to usersWhoLiked,
            "userWhoDisliked" to usersWhoDisliked

        )
    }
}

