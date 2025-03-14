package com.deixebledenkaito.autotechmanuals.data.repository

import android.util.Log
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
// data/repository/StorageRepository.kt
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun eliminarCarpetaStorage(rutaCarpeta: String): Result<Boolean> {
        return try {
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
}