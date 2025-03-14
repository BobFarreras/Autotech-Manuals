package com.deixebledenkaito.autotechmanuals.data.service


import android.util.Log
import com.deixebledenkaito.autotechmanuals.data.repository.AportacioRepository
import com.deixebledenkaito.autotechmanuals.data.repository.ManualAportacioRepository
import com.deixebledenkaito.autotechmanuals.data.repository.StorageRepository
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

// data/service/AportacioService.kt
class AportacioService @Inject constructor(
    private val aportacioRepository: AportacioRepository,
    private val manualAportacioRepository: ManualAportacioRepository,
    private val storageRepository: StorageRepository
) {
    suspend fun getAportacionsByUser(userId: String): List<AportacioUser> {
        return when (val result = aportacioRepository.getAportacionsByUser(userId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("AportacioService", "Error obtenint aportacions: ${result.message}")
                emptyList()
            }
        }
    }

    suspend fun addAportacio(userId: String, aportacio: AportacioUser): Result<Boolean> {
        return try {
            val success = aportacioRepository.addAportacio(userId, aportacio)
            Result.Success(success)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun addAportacioEnElManual(aportacio: AportacioUser): Boolean {
        return when (val result = manualAportacioRepository.addAportacioEnElManual(aportacio)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("AportacioService", "Error afegint aportació al manual: ${result.message}")
                false
            }
        }
    }

    suspend fun eliminarAportacio(userId: String, aportacio: AportacioUser): Boolean {
        return try {
            // Eliminar l'aportació de la col·lecció d'usuaris
            val result1 = aportacioRepository.eliminarAportacio(userId, aportacio)

            // Eliminar l'aportació de la col·lecció de manuals
            val result2 = manualAportacioRepository.eliminarAportacio(aportacio)

            // Eliminar la carpeta de Storage
            val result3 = storageRepository.eliminarCarpetaStorage("usuaris/$userId/aportacions/${aportacio.id}")

            // Retornar true si totes les operacions van ser exitoses
            if (result1 is Result.Success && result2 is Result.Success && result3 is Result.Success) {
                true
            } else {
                Log.e("AportacioService", "Error eliminant aportació")
                false
            }
        } catch (e: Exception) {
            Log.e("AportacioService", "Error eliminant aportació: ${e.message}")
            false
        }
    }

    suspend fun loadModelAndAportacions(manualId: String, modelId: String): Pair<org.opencv.dnn.Model?, List<AportacioUser>> {
        return when (val result = manualAportacioRepository.loadModelAndAportacions(manualId, modelId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("AportacioService", "Error carregant model i aportacions: ${result.message}")
                Pair(null, emptyList())
            }
        }
    }
}