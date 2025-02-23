package com.deixebledenkaito.autotechmanuals.data.network.firebstore

import android.net.Uri
import android.util.Log
import com.deixebledenkaito.autotechmanuals.data.response.AportacioResponse
import com.deixebledenkaito.autotechmanuals.data.response.ManualResponse
import com.deixebledenkaito.autotechmanuals.data.response.ModelResponse
import com.deixebledenkaito.autotechmanuals.data.response.TopManualsResponse
import com.deixebledenkaito.autotechmanuals.data.response.UserResponse
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.domain.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


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
    private val auth: FirebaseAuth,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    companion object {
        const val MANUALS_PATH = "manuals"
        const val USUARIS_PATH = "usuaris"
        const val TOP_MANUALS_PATH = "top_manuals"
        const val MODELS_PATH = "models"
        const val MANAGAMENTS_PATH = "managaments"
        const val APORTACIONS_PATH = "aportacions"
    }


    // Memòria cau per a manuals destacats
    private var cachedTopManuals: List<String>? = null

    // Memòria cau per a tots els manuals
    private var cachedManuals: List<Manuals>? = null

    suspend fun getTopManuals(): List<String> {
        return try {
            // Si ja tenim les dades en memòria cau, les retornem
            cachedTopManuals?.let { return it }

            // Si no, fem la consulta a Firestore
            val document = firestore.collection(MANAGAMENTS_PATH)
                .document(TOP_MANUALS_PATH)
                .get()
                .await()

            val topManuals = document.toObject(TopManualsResponse::class.java)?.ids ?: emptyList()

            // Emmagatzemem les dades en memòria cau
            cachedTopManuals = topManuals

            topManuals
        } catch (e: Exception) {
            println("Error en obtenir els manuals destacats: ${e.message}")
            emptyList()
        }
    }

    suspend fun totsElsManuals(): List<Manuals> {
        return try {
            // Si ja tenim les dades en memòria cau, les retornem
            cachedManuals?.let { return it }

            // Si no, fem la consulta a Firestore
            val manuals = firestore.collection(MANUALS_PATH)
                .get()
                .await()
                .toObjects(ManualResponse::class.java)
                .map { it.toDomain() }

            // Emmagatzemem les dades en memòria cau
            cachedManuals = manuals

            manuals
        } catch (e: Exception) {
            println("Error en obtenir tots els manuals: ${e.message}")
            emptyList()
        }
    }

    // Funció per invalidar la memòria cau (per exemple, quan es vol forçar una actualització)
    fun invalidateCache() {
        cachedTopManuals = null
        cachedManuals = null
    }
    suspend fun incrementManualUsage(manualId: String) {
        val manualRef = firestore.collection(MANUALS_PATH).document(manualId)
        // 1. Actualitzar el comptador d'ús del manual dins de la transacció
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(manualRef)
            val currentUsageCount = snapshot.getLong("usageCount") ?: 0
            val newUsageCount = currentUsageCount + 1
            transaction.update(manualRef, "usageCount", newUsageCount)
        }.await()
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
    suspend fun getManualsByUser(userId: String): List<Manuals> {
        return try {
            firestore.collection(MANUALS_PATH)
                .whereEqualTo("userId", userId) // Assumim que cada manual té un camp "userId"
                .get()
                .await()
                .toObjects(ManualResponse::class.java)
                .map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint manuals de l'usuari: ${e.message}")
            emptyList()
        }
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
    suspend fun afegirManual(nom: String, descripcio: String, imageUrl: String, usageCount : Long): Boolean {
        val id = generateManualId()
        val manual = hashMapOf(
            "id" to id,
            "nom" to nom,
            "descripcio" to descripcio,
            "imageUrl" to imageUrl,
            "usageCount" to usageCount

        )
        return suspendCancellableCoroutine { continuation ->
            // Crear el document a la col·lecció MANUALS_PATH
            firestore.collection(MANUALS_PATH).document(id).set(manual)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Crear la subcol·lecció MODELS_PATH dins del document creat
                        firestore.collection(MANUALS_PATH).document(id).collection(MODELS_PATH)
                            .document("placeholder") // Document temporal (opcional)
                            .set(mapOf("createdAt" to System.currentTimeMillis()))
                            .addOnCompleteListener { subTask ->
                                if (subTask.isSuccessful) {
                                    continuation.resume(true) // Tot correcte
                                } else {
                                    continuation.resume(false) // Error en crear la subcol·lecció
                                }
                            }
                    } else {
                        continuation.resume(false) // Error en crear el document principal
                    }
                }
                .addOnFailureListener {
                    continuation.resume(false) // Error en crear el document principal
                }
        }
    }

    // Funció per generar un ID únic per a un manual
    private fun generateManualId(): String {
        return Date().time.toString()
    }

    suspend fun getModelsForManual(manualName: String): List<Model> {
        return firestore.collection(MANUALS_PATH).document(manualName).collection(MODELS_PATH).get()
            .await().map { document ->
            document.toObject(ModelResponse::class.java).toDomainModel()
        }
    }

    fun updateLastUsedManual(manualName: String) {
        sharedPreferencesHelper.saveLastUsedManual(manualName)
    }

    fun getLastUsedManual(): String? {
        return sharedPreferencesHelper.getLastUsedManual()
    }

    suspend fun getManualByName(manualName: String): Manuals? {
        return firestore.collection(MANUALS_PATH).document(manualName)
            .get()
            .await()
            .toObject(ManualResponse::class.java) // Deserialitza a ManualResponse
            ?.toDomain() // Converteix a Manuals
    }

    suspend fun saveUserData(user: User): Boolean {
        return try {
            // Crear un document a la col·lecció USUARIS_PATH amb l'ID de l'usuari
            firestore.collection(USUARIS_PATH)
                .document(user.id)
                .set(
                    mapOf(
                        "id" to user.id,
                        "name" to user.name,
                        "email" to user.email,
                        "profileImageUrl" to user.profileImageUrl,
                        "description" to user.description
                    )
                )
                .await()
            true // Retornem true si s'ha guardat correctament
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error guardant dades de l'usuari: ${e.message}")
            false // Retornem false si hi ha hagut un error
        }
    }


    suspend fun getAportacionsByUser(userId: String): List<AportacioUser> {
        return try {
            firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .get()
                .await()
                .toObjects(AportacioUser::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint aportacions: ${e.message}")
            emptyList()
        }
    }

    suspend fun addAportacio(userId: String, aportacio: AportacioUser): Boolean {
        return try {
            firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .document(aportacio.id)
                .set(aportacio.toFirestore())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error afegint aportació: ${e.message}")
            false
        }
    }
    suspend fun addAportacioEnElManual(userId: String, aportacio: AportacioUser): Boolean {
        return try {
            firestore.collection(MANUALS_PATH)
                .document(aportacio.manual)
                .collection(MODELS_PATH)
                .document(aportacio.model)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .set(aportacio.toFirestore())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error afegint aportació al manual: ${e.message}")
            false
        }
    }

    suspend fun eliminarAportacio(userId: String, aportacioId: String): Boolean {
        return try {
            // 1️⃣ Eliminar l'aportació de Firestore
            firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .document(aportacioId)
                .delete()
                .await()

            // 2️⃣ Eliminar la carpeta de Storage
            eliminarCarpetaStorage("usuaris/$userId/aportacions/$aportacioId")

            true // Eliminació correcta
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error eliminant aportació: ${e.message}")
            false // Error durant l'eliminació
        }
    }

    private suspend fun eliminarCarpetaStorage(rutaCarpeta: String) {
        try {
            val listResult = storage.reference.child(rutaCarpeta).listAll().await()

            // Eliminar tots els fitxers dins la carpeta
            listResult.items.forEach { it.delete().await() }

            // Eliminar subcarpetes (si n'hi ha)
            listResult.prefixes.forEach { eliminarCarpetaStorage(it.path) }

            Log.d("FirebaseDataBaseService", "Carpeta eliminada: $rutaCarpeta")
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error eliminant carpeta $rutaCarpeta: ${e.message}")
        }
    }








}