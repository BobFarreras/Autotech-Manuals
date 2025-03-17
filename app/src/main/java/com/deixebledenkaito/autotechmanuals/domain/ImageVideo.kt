package com.deixebledenkaito.autotechmanuals.domain

data class ImageVideo(
    val imageUrl: String = "", // URL de la imatge o miniatura del vídeo
    val videoUrl: String? = null // URL del vídeo complet (opcional)
){

}