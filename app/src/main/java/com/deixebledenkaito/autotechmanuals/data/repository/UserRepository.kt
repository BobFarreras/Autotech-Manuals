package com.deixebledenkaito.autotechmanuals.data.repository

import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.google.firebase.firestore.auth.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) {
    suspend fun getUser(): com.deixebledenkaito.autotechmanuals.domain.User? {
        return firebaseDataBaseService.getUser()
    }
}