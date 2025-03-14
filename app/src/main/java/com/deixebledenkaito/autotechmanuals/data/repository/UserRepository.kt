package com.deixebledenkaito.autotechmanuals.data.repository

import android.net.Uri
import android.util.Log
import com.deixebledenkaito.autotechmanuals.data.Path.USUARIS_PATH
import com.deixebledenkaito.autotechmanuals.data.response.UserResponse
import com.deixebledenkaito.autotechmanuals.domain.User
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.deixebledenkaito.autotechmanuals.utils.Result
import javax.inject.Inject

// data/repository/UserRepository.kt
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) {
    suspend fun getUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            firestore.collection(USUARIS_PATH)
                .document(firebaseUser.uid)
                .get()
                .await()
                .toObject(UserResponse::class.java)
                ?.toDomain()
        } else {
            null
        }
    }

    suspend fun uploadAndDownloadImage(uri: Uri, userId: String): Result<String> {
        return try {
            val reference = storage.reference.child("usuaris/$userId/imgPerfil/perfil.png")
            val uploadTask = reference.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun saveUserData(user: User): Result<Boolean> {
        return try {
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
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

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
}