package com.deixebledenkaito.autotechmanuals.data.repository


import android.util.Log
import com.deixebledenkaito.autotechmanuals.data.Path.MANUALS_PATH
import com.deixebledenkaito.autotechmanuals.data.Path.MODELS_PATH
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
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

    // Funció per donar like
    suspend fun donarLike(aportacioId: String, userId: String, manualId: String, modelId: String) {
        val userAportacioRef = firestore.collection("usuaris").document(userId)
            .collection("aportacions").document(aportacioId)
        val manualAportacioRef = firestore.collection("manuals").document(manualId)
            .collection("models").document(modelId)
            .collection("aportacions").document(aportacioId)

        firestore.runTransaction { transaction ->
            // Obtenim l'aportació de l'usuari
            val userAportacio = transaction.get(userAportacioRef).toObject(AportacioUser::class.java)
            // Obtenim l'aportació del manual/model
            val manualAportacio = transaction.get(manualAportacioRef).toObject(AportacioUser::class.java)

            if (userAportacio != null && manualAportacio != null) {
                // Verifica si l'usuari ja ha donat like
                if (!userAportacio.usersWhoLiked.contains(userId)) {
                    // Afegeix l'usuari a la llista de likes
                    userAportacio.usersWhoLiked.add(userId)
                    manualAportacio.usersWhoLiked.add(userId)
                    // Incrementa el comptador de likes
                    userAportacio.likes++
                    manualAportacio.likes++
                    // Si l'usuari havia donat dislike abans, elimina'l
                    if (userAportacio.usersWhoDisliked.contains(userId)) {
                        userAportacio.usersWhoDisliked.remove(userId)
                        manualAportacio.usersWhoDisliked.remove(userId)
                        userAportacio.noLikes--
                        manualAportacio.noLikes--
                    }
                    // Actualitza les aportacions a Firestore
                    transaction.set(userAportacioRef, userAportacio)
                    transaction.set(manualAportacioRef, manualAportacio)
                    Log.d("DonarLike", "Like donat correctament a l'aportació $aportacioId")
                } else {
                    Log.d("DonarLike", "L'usuari ja havia donat like a l'aportació $aportacioId")
                }
            } else {
                Log.e("DonarLike", "No s'ha trobat l'aportació a l'usuari o al manual/model")
            }
        }.await()
    }

    // Funció per donar dislike
    suspend fun donarDislike(aportacioId: String, userId: String, manualId: String, modelId: String) {
        val userAportacioRef = firestore.collection("usuaris").document(userId)
            .collection("aportacions").document(aportacioId)
        val manualAportacioRef = firestore.collection("manuals").document(manualId)
            .collection("models").document(modelId)
            .collection("aportacions").document(aportacioId)

        firestore.runTransaction { transaction ->
            // Obtenim l'aportació de l'usuari
            val userAportacio = transaction.get(userAportacioRef).toObject(AportacioUser::class.java)
            // Obtenim l'aportació del manual/model
            val manualAportacio = transaction.get(manualAportacioRef).toObject(AportacioUser::class.java)

            if (userAportacio != null && manualAportacio != null) {
                // Verifica si l'usuari ja ha donat dislike
                if (!userAportacio.usersWhoDisliked.contains(userId)) {
                    // Afegeix l'usuari a la llista de dislikes
                    userAportacio.usersWhoDisliked.add(userId)
                    manualAportacio.usersWhoDisliked.add(userId)
                    // Incrementa el comptador de dislikes
                    userAportacio.noLikes++
                    manualAportacio.noLikes++
                    // Si l'usuari havia donat like abans, elimina'l
                    if (userAportacio.usersWhoLiked.contains(userId)) {
                        userAportacio.usersWhoLiked.remove(userId)
                        manualAportacio.usersWhoLiked.remove(userId)
                        userAportacio.likes--
                        manualAportacio.likes--
                    }
                    // Actualitza les aportacions a Firestore
                    transaction.set(userAportacioRef, userAportacio)
                    transaction.set(manualAportacioRef, manualAportacio)
                    Log.d("DonarDislike", "Dislike donat correctament a l'aportació $aportacioId")
                } else {
                    Log.d("DonarDislike", "L'usuari ja havia donat dislike a l'aportació $aportacioId")
                }
            } else {
                Log.e("DonarDislike", "No s'ha trobat l'aportació a l'usuari o al manual/model")
            }
        }.await()
    }

}