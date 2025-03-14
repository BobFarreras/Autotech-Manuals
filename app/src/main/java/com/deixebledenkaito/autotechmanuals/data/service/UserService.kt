package com.deixebledenkaito.autotechmanuals.data.service


import android.net.Uri
import com.deixebledenkaito.autotechmanuals.data.repository.UserRepository
import com.deixebledenkaito.autotechmanuals.domain.User
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

// data/service/UserService.kt
class UserService @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun getUser(): Result<User?> {
        return try {
            val user = userRepository.getUser()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut",  AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun uploadAndDownloadImage(uri: Uri, userId: String): Result<String> {
        return userRepository.uploadAndDownloadImage(uri, userId)
    }

    suspend fun saveUserData(user: User): Result<Boolean> {
        return userRepository.saveUserData(user)
    }

    suspend fun updateUserProfileImage(userId:String, imageUrl: String): Boolean {
        return userRepository.updateUserProfileImage(userId,imageUrl)
    }
}