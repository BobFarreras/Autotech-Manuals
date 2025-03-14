package com.deixebledenkaito.autotechmanuals.data.service

import com.deixebledenkaito.autotechmanuals.data.repository.AuthRepository
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

// data/service/AuthService.kt
class AuthService @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = authRepository.login(email, password)
            result
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(e.message ?: "Error desconegut",AuthErrorType.INVALID_CREDENTIALS)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.USER_NOT_FOUND)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.UNKNOWN_ERROR)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return authRepository.register(email, password)
    }

    suspend fun loginWithPhone(
        phoneNumber: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ): Result<Unit> {
        return authRepository.loginWithPhone(phoneNumber, callback)
    }

    suspend fun verifyCode(verificationId: String, code: String): Result<FirebaseUser> {
        return authRepository.verifyCode(verificationId, code)
    }

    fun isUserLogged(): Boolean {
        return authRepository.isUserLogged()
    }

    fun logout() {
        authRepository.logout()
    }
}