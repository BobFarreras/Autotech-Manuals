package com.deixebledenkaito.autotechmanuals.data.repository

import com.deixebledenkaito.autotechmanuals.data.Path.APORTACIONS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MANUALS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MODELS_PATH
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
import org.opencv.dnn.Model

// data/repository/ManualAportacioRepository.kt
class ManualAportacioRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun addAportacioEnElManual(aportacio: AportacioUser): Result<Boolean> {
        return try {
            firestore.collection(MANUALS_PATH)
                .document(aportacio.manual)
                .collection(MODELS_PATH)
                .document(aportacio.model)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .set(aportacio.toFirestore())
                .await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun loadModelAndAportacions(manualId: String, modelId: String): Result<Pair<Model?, List<AportacioUser>>> {
        return try {
            // Carregar el model
            val model = firestore.collection(MANUALS_PATH)
                .document(manualId)
                .collection(MODELS_PATH)
                .document(modelId)
                .get()
                .await()
                .toObject(Model::class.java)

            // Carregar les aportacions
            val aportacions = firestore.collection(MANUALS_PATH)
                .document(manualId)
                .collection(MODELS_PATH)
                .document(modelId)
                .collection(APORTACIONS_PATH)
                .get()
                .await()
                .toObjects(AportacioUser::class.java)

            Result.Success(Pair(model, aportacions))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun eliminarAportacio(aportacio: AportacioUser): Result<Boolean> {
        return try {
            firestore.collection(MANUALS_PATH)
                .document(aportacio.manual)
                .collection(MODELS_PATH)
                .document(aportacio.model)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .delete()
                .await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }
}