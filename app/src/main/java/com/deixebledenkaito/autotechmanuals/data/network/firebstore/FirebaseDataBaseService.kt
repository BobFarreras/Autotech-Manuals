package com.deixebledenkaito.autotechmanuals.data.network.firebstore

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import coil.request.ImageRequest

import com.deixebledenkaito.autotechmanuals.data.response.ErrorsDelModelResponse
import com.deixebledenkaito.autotechmanuals.data.response.ManualResponse
import com.deixebledenkaito.autotechmanuals.data.response.RutaGuardadaResponse

import com.deixebledenkaito.autotechmanuals.data.response.TopManualsResponse
import com.deixebledenkaito.autotechmanuals.data.response.UserResponse
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.ErrorsDelModel
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.domain.User


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("IMPLICIT_CAST_TO_ANY")
class FirebaseDataBaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,

) {

    companion object {
        const val MANUALS_PATH = "manuals"
        const val USUARIS_PATH = "usuaris"
        const val TOP_MANUALS_PATH = "top_manuals"
        const val MODELS_PATH = "models"
        const val MANAGAMENTS_PATH = "managaments"
        const val APORTACIONS_PATH = "aportacions"
    }

    //=========================== FUNCIONS DEL MANUAL  =========================================
    // Memòria cau per a manuals
    private var cachedManuals: List<Manuals>? = null
    private var cachedTopManuals: List<String>? = null

    // Funció genèrica per obtenir dades de Firestore
    private suspend inline fun <reified T> getFirestoreData(
        collectionPath: String,
        documentPath: String? = null,
        crossinline mapper: (Any) -> T
    ): T? {
        return try {
            val document = if (documentPath != null) {
                firestore.collection(collectionPath).document(documentPath).get().await()
            } else {
                firestore.collection(collectionPath).get().await()
            }
            mapper(document)
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint dades de Firestore: ${e.message}")
            null
        }
    }


    // Obtenir tots els manuals
    suspend fun totsElsManuals(): List<Manuals> {
        return cachedManuals ?: run {
            try {
                val manuals = withTimeout(5000) { // Timeout de 5 segons
                    getFirestoreData(MANUALS_PATH) { snapshot ->
                        (snapshot as QuerySnapshot).toObjects(ManualResponse::class.java).map { it.toDomain() }
                    } ?: emptyList()
                }
                cachedManuals = manuals
                manuals
            } catch (e: TimeoutCancellationException) {
                Log.e("FirebaseDataBaseService", "Timeout carregant manuals")
                emptyList()
            }
        }
    }
    // Obtenir els manuals destacats
    suspend fun getTopManuals(): List<String> {
        return cachedTopManuals ?: run {
            val topManuals = getFirestoreData(MANAGAMENTS_PATH, TOP_MANUALS_PATH) { snapshot ->
                (snapshot as DocumentSnapshot).toObject(TopManualsResponse::class.java)?.ids ?: emptyList()
            } ?: emptyList()
            cachedTopManuals = topManuals
            topManuals
        }
    }
    // Invalida la memòria cau
    fun invalidateCache() {
        cachedManuals = null
        cachedTopManuals = null
        cachedModels.clear()
    }
    fun isCacheValid(): Boolean {
        return cachedManuals != null || cachedTopManuals != null || cachedModels.isNotEmpty()
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
    //    DESCARREGAR ULTIM MANUAL UTLITZAT
    suspend fun getLastUsedManual(): String? {
        return try {
            val document = firestore.collection("ultimClickManual")
                .document("lastManual")
                .get()
                .await()
            document.getString("manualName")
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint l'últim manual utilitzat: ${e.message}")
            null
        }
    }
    //    IMPLEMENTAR A FIREBASE ULTIM MANUAL UTILTIZAT
    suspend fun updateLastUsedManual(manualName: String): Boolean {
        return try {
            val data = hashMapOf("manualName" to manualName)
            firestore.collection("ultimClickManual")
                .document("lastManual")
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error guardant l'últim manual utilitzat: ${e.message}")
            false
        }
    }
    // Funció per obtenir un manual pel seu nom i pre-carregar els models a la memòria cau
    suspend fun getManualByName(manualName: String): Manuals? {
        return try {
            val document = firestore.collection("manuals")
                .whereEqualTo("nom", manualName)
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()

            document?.toObject(ManualResponse::class.java)?.toDomain() // Assigna la imatge local aquí
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint el manual per nom: ${e.message}")
            null
        }
    }
    //=========================== FUNCIONS DEL USER =========================================
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
    // Funció per pujar i descarregar una imatge
    suspend fun uploadAndDownloadImage(uri: Uri, usuariId: String): String {
        return suspendCancellableCoroutine { continuation ->
            val reference = storage.reference.child("usuaris/${usuariId}/imgPerfil/perfil.png")
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
    suspend fun eliminarAportacio(userId: String, aportacio: AportacioUser): Boolean {
        return try {
            // 1️⃣ Eliminar l'aportació de la col·lecció d'usuaris
            firestore.collection("usuaris")
                .document(userId)
                .collection("aportacions")
                .document(aportacio.id)
                .delete()
                .await()

            // 2️⃣ Eliminar l'aportació de la col·lecció de manuals
            firestore.collection(MANUALS_PATH)
                .document(aportacio.manual)
                .collection(MODELS_PATH)
                .document(aportacio.model)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .delete()
                .await()

            // 3️⃣ Eliminar la carpeta de Storage (si existeix)
            eliminarCarpetaStorage("usuaris/$userId/aportacions/${aportacio.id}")

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

    //=========================== FUNCIONS DEL BTN ERRORS =========================================
    // Buscar un error pel número
    suspend fun buscarErrorPerNumero(manualId: String, modelId: String, numero: String): ErrorsDelModel? {
        return try {
            // Consulta a Firestore
            val querySnapshot = firestore.collection("manuals")
                .document(manualId)
                .collection("models")
                .document(modelId)
                .collection("errors")
                .whereEqualTo("numero", numero) // Buscar per número
                .get()
                .await()

            // Comprovar si s'ha trobat algun error
            if (querySnapshot.isEmpty) {
                null // No s'ha trobat cap error
            } else {
                // Convertir el primer document trobat a ErrorsDelModel
                querySnapshot.documents[0].toObject(ErrorsDelModelResponse::class.java)?.toDomain()
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error cercant l'error: ${e.message}")
            null
        }
    }

    //=========================== FUNCIONS DEL PDF MODELS =========================================
    suspend fun obtenirPdfsDelModel(manualId: String, modelId: String): List<StorageReference> {
        return try {
            val pdfsRef = storage.reference.child("manuals/$manualId/$modelId/pdfManuals")
            val listResult = pdfsRef.listAll().await()
            Log.d("StoragePdf", "PDFs trobats: ${listResult.items.size}")
            listResult.items // Retorna la llista de PDFs
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint PDFs: ${e.message}")
            emptyList()
        }
    }
    suspend fun obtenirPdfsDeLaCarpinteria(manualId: String, modelId: String, carpinteriaId:String): List<StorageReference> {
        return try {
            val pdfsRef = storage.reference.child("manuals/$manualId/$modelId/pdfCarpinteria/$carpinteriaId")
            val listResult = pdfsRef.listAll().await()
            Log.d("StoragePdf", "PDFs trobats: ${listResult.items.size}")
            listResult.items // Retorna la llista de PDFs
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint PDFs: ${e.message}")
            emptyList()
        }
    }
    // Descàrrega d'un PDF
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun descarregarPdf(pdfRef: StorageReference, context: Context): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                // Obtenir el nom del fitxer
                val fileName = pdfRef.name

                // Crear un fitxer a la carpeta de descàrregues utilitzant MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                // Inserir el fitxer a la carpeta de descàrregues
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    // Descarregar el PDF directament a un fitxer temporal
                    val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
                    pdfRef.getFile(tempFile).await()

                    // Copiar el contingut del fitxer temporal a la ubicació final
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        tempFile.inputStream().copyTo(outputStream)
                    }

                    // Eliminar el fitxer temporal
                    tempFile.delete()

                    Log.d("FirebaseDataBaseService", "PDF descarregat correctament: $uri")
                    uri
                } else {
                    Log.e("FirebaseDataBaseService", "No s'ha pogut crear el fitxer a la carpeta de descàrregues.")
                    null
                }
            } catch (e: Exception) {
                Log.e("FirebaseDataBaseService", "Error descarregant PDF: ${e.message}", e)
                null
            }
        }
    }

    //=========================== FUNCIONS DEL PROFILE =========================================
    suspend fun updateUserProfileImage(userId: String, imageUrl: String): Boolean {
        return try {
            firestore.collection(USUARIS_PATH)
                .document(userId)
                .update("profileImageUrl", imageUrl)
                .await()
            true // Retornem true si s'ha actualitzat correctament
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error actualitzant la imatge de perfil: ${e.message}")
            false // Retornem false si hi ha hagut un error
        }
    }

    //=========================== FUNCIONS DEL MODEL =========================================
    // Memòria cau per a models (clau: manualName, valor: llista de models)
    private var cachedModels: MutableMap<String, List<Model>> = mutableMapOf()

    // Funció per obtenir els models d'un manual amb memòria cau
    suspend fun getModelsForManual(manualName: String): List<Model> {
        return cachedModels[manualName] ?: run {
            val models = try {
                firestore.collection(MANUALS_PATH)
                    .document(manualName)
                    .collection(MODELS_PATH)
                    .get()
                    .await()
                    .toObjects(Model::class.java)
            } catch (e: Exception) {
                Log.e("FirebaseDataBaseService", "Error obtenint models: ${e.message}")
                emptyList()
            }
            cachedModels[manualName] = models // Emmagatzemar a la memòria cau
            models
        }
    }
    // Funció per obtenir les aportacions d'un model
    suspend fun getAportacionsByModel(modelId: String): List<AportacioUser> {
        return try {
            firestore.collection("aportacions")
                .whereEqualTo("modelId", modelId)
                .get()
                .await()
                .toObjects(AportacioUser::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint aportacions: ${e.message}")
            emptyList()
        }
    }
    // Invalida la memòria cau dels models d'un manual específic
    private fun invalidateModelsCache(manualName: String) {
        cachedModels.remove(manualName)
    }
    suspend fun addModelToManual(manualName: String, model: Model): Boolean {
        return try {
            firestore.collection(MANUALS_PATH)
                .document(manualName)
                .collection(MODELS_PATH)
                .document(model.id)
                .set(model)
                .await()

            // Invalida la memòria cau dels models per aquest manual
            invalidateModelsCache(manualName)
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error afegint model: ${e.message}")
            false
        }
    }

    //=========================== FUNCIONS DE LA APORTACIO MODEL =========================================
    // Funció per carregar un model i les seves aportacions
    suspend fun loadModelAndAportacions(manualId: String, modelId: String): Pair<Model?, List<AportacioUser>> {
        return try {
            // Carregar el model
            val model = firestore.collection(
                MANUALS_PATH)
                .document(manualId)
                .collection(MODELS_PATH)
                .document(modelId)
                .get()
                .await()
                .toObject(Model::class.java)

            // Carregar les aportacions
            val aportacions = firestore.collection(  MANUALS_PATH)
                .document(manualId)
                .collection(MODELS_PATH)
                .document(modelId)
                .collection(APORTACIONS_PATH)
                .get()
                .await()
                .toObjects(AportacioUser::class.java)

            Pair(model, aportacions)
        } catch (e: Exception) {
            throw Exception("Error carregant dades: ${e.message}")
        }
    }

    // Funció per eliminar una aportació
    suspend fun eliminarAportacio(aportacio: AportacioUser): Boolean {
        return try {
            firestore.collection(MANUALS_PATH)
                .document(aportacio.manual)
                .collection(MODELS_PATH)
                .document(aportacio.model)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            throw Exception("Error eliminant aportació: ${e.message}")
        }
    }

    // Funció per actualitzar likes o no likes
    suspend fun updateLikeOrNoLike(aportacio: AportacioUser, userId: String, isLike: Boolean): AportacioUser {
        return try {
            val updatedAportacio = toggleLikeOrNoLike(aportacio, userId, isLike)
            firestore.collection(MANUALS_PATH)
                .document(aportacio.manual)
                .collection(MODELS_PATH)
                .document(aportacio.model)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .set(updatedAportacio.toFirestore())
                .await()
            firestore.collection(USUARIS_PATH)
                .document(userId)
                .collection(APORTACIONS_PATH)
                .document(aportacio.id)
                .set(updatedAportacio.toFirestore())
                .await()
            updatedAportacio
        } catch (e: Exception) {
            throw Exception("Error actualitzant Like/No Like: ${e.message}")
        }
    }

    // Funció auxiliar per gestionar likes i no likes
    private fun toggleLikeOrNoLike(aportacio: AportacioUser, userId: String, isLike: Boolean): AportacioUser {
        val updatedAportacio = aportacio.copy()

        val hasLiked = updatedAportacio.usersWhoLiked.contains(userId)
        val hasDisliked = updatedAportacio.usersWhoDisliked.contains(userId)

        if (isLike) {
            if (hasDisliked) {
                // Si l'usuari havia donat No Like, canviar a Like
                updatedAportacio.usersWhoDisliked.remove(userId)
                updatedAportacio.noLikes -= 1
                updatedAportacio.usersWhoLiked.add(userId)
                updatedAportacio.likes += 1
            } else if (!hasLiked) {
                // Si l'usuari no havia votat, afegir Like
                updatedAportacio.usersWhoLiked.add(userId)
                updatedAportacio.likes += 1
            }
            // Si ja havia donat Like, no fer res
        } else {
            if (hasLiked) {
                // Si l'usuari havia donat Like, canviar a No Like
                updatedAportacio.usersWhoLiked.remove(userId)
                updatedAportacio.likes -= 1
                updatedAportacio.usersWhoDisliked.add(userId)
                updatedAportacio.noLikes += 1
            } else if (!hasDisliked) {
                // Si l'usuari no havia votat, afegir No Like
                updatedAportacio.usersWhoDisliked.add(userId)
                updatedAportacio.noLikes += 1
            }
            // Si ja havia donat No Like, no fer res
        }

        return updatedAportacio
    }

    suspend fun guardarRuta(userId: String, ruta: RutaGuardada): Boolean {
        return try {
            firestore.collection("usuaris")
                .document(userId)
                .collection("rutesGuardades")
                .document(ruta.id)
                .set(ruta.toFirestore())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error guardant ruta: ${e.message}")
            false
        }
    }

    suspend fun obtenirRutesGuardades(userId: String): List<RutaGuardada> {
        return try {
            firestore.collection("usuaris")
                .document(userId)
                .collection("rutesGuardades")
                .get()
                .await()
                .toObjects(RutaGuardadaResponse::class.java) // Deserialitza com a RutaGuardadaResponse
                .map { it.toDomain() } // Converteix a RutaGuardada
        } catch (e: Exception) {
            Log.e("FirebaseDataBaseService", "Error obtenint rutes guardades: ${e.message}")
            emptyList()
        }
    }




 }

