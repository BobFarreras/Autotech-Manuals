package com.deixebledenkaito.autotechmanuals.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.deixebledenkaito.autotechmanuals.domain.ImageVideo
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

// data/repository/StorageRepository.kt
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) {



    // Eliminar una carpeta de Storage
    suspend fun eliminarCarpetaStorage(rutaCarpeta: String): Result<Boolean> {
        return try {
            Log.d("StorageRepository", "Eliminant carpeta: $rutaCarpeta")
            val listResult = storage.reference.child(rutaCarpeta).listAll().await()

            // Eliminar tots els fitxers dins la carpeta
            listResult.items.forEach { it.delete().await() }

            // Eliminar subcarpetes (si n'hi ha)
            listResult.prefixes.forEach { eliminarCarpetaStorage(it.path) }

            Log.d("StorageRepository", "Carpeta eliminada: $rutaCarpeta")
            Result.Success(true)
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error eliminant carpeta $rutaCarpeta: ${e.message}")
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    // Pujar un fitxer i retornar la seva URL
    suspend fun pujarFitxer(uri: Uri, userId: String, aportacioId: String, tipus: String): String? {
        return try {
            Log.d("StorageRepository", "Pujant fitxer de tipus: $tipus")
            val fitxerName = UUID.randomUUID().toString()
            val fitxerRef = storage.reference.child("usuaris/$userId/aportacions/$aportacioId/$tipus/$fitxerName")
            val uploadTask = fitxerRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            Log.d("StorageRepository", "Fitxer pujat correctament: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error pujant fitxer: ${e.message}")
            null
        }
    }

    // Generar una miniatura a partir d'un vídeo
    private suspend fun generarMiniatura(context: Context, videoUri: Uri, userId: String, aportacioId: String): String? {
        return try {
            Log.d("StorageRepository", "Generant miniatura per al vídeo: $videoUri")
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoUri)
            val frame = retriever.frameAtTime
            retriever.release()

            if (frame != null) {
                val outputStream = ByteArrayOutputStream()
                frame.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                val thumbnailBytes = outputStream.toByteArray()

                val thumbnailName = "thumbnail_${System.currentTimeMillis()}.jpg"
                val thumbnailRef = storage.reference.child("usuaris/$userId/aportacions/$aportacioId/miniatures/$thumbnailName")
                thumbnailRef.putBytes(thumbnailBytes).await()
                val downloadUrl = thumbnailRef.downloadUrl.await().toString()
                Log.d("StorageRepository", "Miniatura generada correctament: $downloadUrl")
                downloadUrl
            } else {
                Log.e("StorageRepository", "No s'ha pogut extreure el fotograma del vídeo")
                null
            }
        } catch (e: Exception) {
            Log.e("StorageRepository", "Error generant miniatura: ${e.message}")
            null
        }
    }

    // Pujar fitxers i generar miniatures per als vídeos
    suspend fun pujarFitxersIGenerarMiniatures(
        context: Context,
        fitxers: List<Uri>,
        userId: String,
        aportacioId: String,
        tipus: String
    ): List<ImageVideo> {
        return fitxers.mapNotNull { uri ->
            when (tipus) {
                "videos" -> {
                    // Pujar el vídeo
                    val videoUrl = pujarFitxer(uri, userId, aportacioId, "videos")
                    // Generar la miniatura
                    val thumbnailUrl = generarMiniatura(context, uri, userId, aportacioId)
                    if (videoUrl != null && thumbnailUrl != null) {
                        ImageVideo(imageUrl = thumbnailUrl, videoUrl = videoUrl)
                    } else {
                        null
                    }
                }
                else -> {
                    // Pujar imatges o PDFs
                    val fileUrl = pujarFitxer(uri, userId, aportacioId, tipus)
                    if (fileUrl != null) {
                        ImageVideo(imageUrl = fileUrl, videoUrl = null)
                    } else {
                        null
                    }
                }
            }
        }
    }
}