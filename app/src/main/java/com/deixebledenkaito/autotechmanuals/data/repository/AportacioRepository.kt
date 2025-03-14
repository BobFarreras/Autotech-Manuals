package com.deixebledenkaito.autotechmanuals.data.repository


import android.util.Log
import com.deixebledenkaito.autotechmanuals.data.Path.APORTACIONS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MANUALS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MODELS_PATH
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

// data/repository/AportacioRepository.kt
class AportacioRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAportacionsByUser(userId: String): Result<List<AportacioUser>> {
        return try {
            val aportacions = firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .get()
                .await()
                .toObjects(AportacioUser::class.java)
            Result.Success(aportacions)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun addAportacio(userId: String, aportacio: AportacioUser): Boolean {
        return try {
            firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .document(aportacio.id)
                .set(aportacio)
                .await()
            true
        } catch (e: Exception) {
            Log.e("AportacioRepository", "Error guardant aportació: ${e.message}")
            false
        }
    }

    suspend fun eliminarAportacio(userId: String, aportacio: AportacioUser): Result<Boolean> {
        return try {
            firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .document(aportacio.id)
                .delete()
                .await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun getAportacionsForModel(manualId: String, modelId: String): List<AportacioUser> {
        return firestore.collection(MANUALS_PATH)
            .document(manualId)
            .collection(MODELS_PATH)
            .document(modelId)
            .collection(APORTACIONS_PATH)
            .get()
            .await()
            .toObjects(AportacioUser::class.java)
    }
}