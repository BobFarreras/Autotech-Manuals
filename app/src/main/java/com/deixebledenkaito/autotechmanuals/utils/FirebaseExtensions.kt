package com.deixebledenkaito.autotechmanuals.utils

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

// utils/FirebaseExtensions.kt
suspend fun <T> DocumentReference.awaitAndMap(mapper: (DocumentSnapshot) -> T): Result<T> {
    return try {
        val snapshot = this.get().await()
        Result.Success(mapper(snapshot))
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
    }
}