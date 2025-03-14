package com.deixebledenkaito.autotechmanuals.data.repository

import com.deixebledenkaito.autotechmanuals.data.Path.RUTES_GUARDADES
import com.deixebledenkaito.autotechmanuals.data.Path.USUARIS_PATH
import com.deixebledenkaito.autotechmanuals.data.response.RutaGuardadaResponse
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

class RutaGuardadaRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun obtenirRutesGuardades(userId: String): Result<List<RutaGuardada>> {
        return try {
            val rutes = firestore.collection(USUARIS_PATH)
                .document(userId)
                .collection(RUTES_GUARDADES)
                .get()
                .await()
                .toObjects(RutaGuardadaResponse::class.java)
                .map { it.toDomain() }
            Result.Success(rutes)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun guardarRuta(userId: String, ruta: RutaGuardada): Result<Boolean> {
        return try {
            firestore.collection(USUARIS_PATH)
                .document(userId)
                .collection(RUTES_GUARDADES)
                .document(ruta.id)
                .set(ruta.toFirestore())
                .await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    // Funció per verificar si una ruta està guardada pel seu contingut
    suspend fun isRutaGuardada(userId: String, rutaContent: String): Boolean {
        return try {
            val query = firestore.collection(USUARIS_PATH)
                .document(userId)
                .collection(RUTES_GUARDADES)
                .whereEqualTo("ruta", rutaContent) // Busca pel camp "ruta"
                .get()
                .await()
            !query.isEmpty // Retorna true si hi ha alguna ruta amb aquest contingut
        } catch (e: Exception) {
            false // En cas d'error, assumim que la ruta no està guardada
        }
    }
}