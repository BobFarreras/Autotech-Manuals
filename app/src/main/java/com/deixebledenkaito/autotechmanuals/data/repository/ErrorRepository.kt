package com.deixebledenkaito.autotechmanuals.data.repository

import com.deixebledenkaito.autotechmanuals.data.Path.ERRORS_ECDRIVE
import com.deixebledenkaito.autotechmanuals.data.Path.MODELS_PATH
import com.deixebledenkaito.autotechmanuals.data.response.ErrorsDelModelResponse
import com.deixebledenkaito.autotechmanuals.domain.ErrorsDelModel
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
// data/repository/ErrorRepository.kt
class ErrorRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun buscarErrorPerNumero(manualId: String, modelId: String, numero: String): Result<ErrorsDelModel?> {
        return try {
            val querySnapshot = firestore.collection("manuals")
                .document(manualId)
                .collection(MODELS_PATH)
                .document(modelId)
                .collection(ERRORS_ECDRIVE)
                .whereEqualTo("numero", numero)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Result.Success(null)
            } else {
                val error = querySnapshot.documents[0].toObject(ErrorsDelModelResponse::class.java)?.toDomain()
                Result.Success(error)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }
}