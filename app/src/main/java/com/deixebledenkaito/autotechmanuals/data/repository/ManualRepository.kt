package com.deixebledenkaito.autotechmanuals.data.repository


import com.deixebledenkaito.autotechmanuals.data.Path.MANAGAMENTS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MANUALS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.TOP_MANUALS_PATH
import com.deixebledenkaito.autotechmanuals.data.response.ManualResponse
import com.deixebledenkaito.autotechmanuals.data.response.TopManualsResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// data/repository/ManualRepository.kt
class ManualRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getManuals(): List<ManualResponse> {
        return firestore.collection(MANUALS_PATH)
            .get()
            .await()
            .toObjects(ManualResponse::class.java)
    }

    suspend fun getTopManuals(): List<String> {
        return firestore.collection(MANAGAMENTS_PATH)
            .document(TOP_MANUALS_PATH)
            .get()
            .await()
            .toObject(TopManualsResponse::class.java)
            ?.ids ?: emptyList()
    }

    suspend fun getLastUsedManual(): String? {
        val document = firestore.collection("ultimClickManual")
            .document("lastManual")
            .get()
            .await()
        return document.getString("manualName")
    }

    suspend fun updateLastUsedManual(manualName: String): Boolean {
        return try {
            val data = hashMapOf("manualName" to manualName)
            firestore.collection("ultimClickManual")
                .document("lastManual")
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getManualByName(manualName: String): ManualResponse? {
        return firestore.collection(MANUALS_PATH)
            .whereEqualTo("nom", manualName)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(ManualResponse::class.java)
    }

    suspend fun incrementManualUsage(manualId: String) {
        val manualRef = firestore.collection(MANUALS_PATH).document(manualId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(manualRef)
            val currentUsageCount = snapshot.getLong("usageCount") ?: 0
            val newUsageCount = currentUsageCount + 1
            transaction.update(manualRef, "usageCount", newUsageCount)
        }.await()
    }
}