package com.deixebledenkaito.autotechmanuals.data.network.firebstore

import android.net.Uri
import com.deixebledenkaito.autotechmanuals.data.response.ManualResponse
import com.deixebledenkaito.autotechmanuals.data.response.TopManualsResponse
import com.deixebledenkaito.autotechmanuals.data.response.UserResponse
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDataBaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    companion object {
        const val MANUALS_PATH = "manuals"
        const val USUARIS_PATH = "usuaris"
        const val TOP_MANUALS_PATH = "top_manuals"
    }

    // Funció per obtenir tots els manuals
    suspend fun totsElsManuals(): List<Manuals> {
        return firestore.collection(MANUALS_PATH).get().await().map { document ->
            document.toObject(ManualResponse::class.java).toDomain()
        }
    }

    // Funció per obtenir l'últim manual afegit
    suspend fun ultimManual(): Manuals? {
        return firestore.collection(MANUALS_PATH)
            .orderBy("id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            .firstOrNull()
            ?.toObject(ManualResponse::class.java)
            ?.toDomain()
    }

    // Funció per obtenir els manuals més populars
    suspend fun topManuals(): List<Manuals> {
        val topIds = firestore.collection(TOP_MANUALS_PATH).document("top").get().await()
            .toObject(TopManualsResponse::class.java)?.ids ?: emptyList()
        return firestore.collection(MANUALS_PATH)
            .whereIn("id", topIds)
            .get()
            .await()
            .map { it.toObject(ManualResponse::class.java).toDomain() }
    }

    // Funció per obtenir les dades de l'usuari
    suspend fun getUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            firestore.collection(USUARIS_PATH).document(firebaseUser.uid).get().await()
                .toObject(UserResponse::class.java)?.toDomain()
        } else {
            null
        }
    }

    // Funció per tancar sessió
    fun logout() {
        auth.signOut()
    }

    // Funció per pujar i descarregar una imatge
    suspend fun uploadAndDownloadImage(uri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val reference = storage.reference.child("manuals/${uri.lastPathSegment}")
            reference.putFile(uri, createMetaData()).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    continuation.resume(uri.toString())
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }
    // Funció per crear metadades de la imatge
    private fun createMetaData(): StorageMetadata {
        return storageMetadata {
            contentType = "image/jpeg"
            setCustomMetadata("date", Date().time.toString())
        }
    }

    // Funció per afegir un nou manual
    suspend fun afegirManual(nom: String, descripcio: String, imageUrl: String): Boolean {
        val id = generateManualId()
        val manual = hashMapOf(
            "id" to id,
            "nom" to nom,
            "descripcio" to descripcio,
            "imageUrl" to imageUrl
        )
        return suspendCancellableCoroutine { continuation ->
            firestore.collection(MANUALS_PATH).document(id).set(manual)
                .addOnCompleteListener {
                    continuation.resume(true)
                }.addOnFailureListener {
                    continuation.resume(false)
                }
        }
    }

    // Funció per generar un ID únic per a un manual
    private fun generateManualId(): String {
        return Date().time.toString()
    }
}