package com.deixebledenkaito.autotechmanuals.data.repository


import com.deixebledenkaito.autotechmanuals.data.Path.MANUALS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MODELS_PATH
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// data/repository/ModelRepository.kt
class ModelRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getModelById(manualId: String, modelId: String): Model {
        return firestore.collection(MANUALS_PATH)
            .document(manualId)
            .collection(MODELS_PATH)
            .document(modelId)
            .get()
            .await()
            .toObject(Model::class.java) ?: throw Exception("Model no trobat")
    }

    suspend fun getModelsForManual(manualId: String): List<Model> {
        return firestore.collection(MANUALS_PATH)
            .document(manualId)
            .collection(MODELS_PATH)
            .get()
            .await()
            .toObjects(Model::class.java)
    }
}