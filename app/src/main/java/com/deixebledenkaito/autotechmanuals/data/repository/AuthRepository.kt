package com.deixebledenkaito.autotechmanuals.data.repository

import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

// data/repository/AuthRepository.kt
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.Success(result.user ?: throw Exception("Usuari no trobat"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Credencials incorrectes", AuthErrorType.INVALID_CREDENTIALS)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error("Usuari no trobat", AuthErrorType.USER_NOT_FOUND)
        } catch (e: Exception) {
            Result.Error("Error desconegut: ${e.message}", AuthErrorType.UNKNOWN_ERROR)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(result.user ?: throw Exception("Usuari no creat"))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.Error("Contrasenya feble", AuthErrorType.WEAK_PASSWORD)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Credencials incorrectes", AuthErrorType.INVALID_CREDENTIALS)
        } catch (e: Exception) {
            Result.Error("Error desconegut: ${e.message}", AuthErrorType.UNKNOWN_ERROR)
        }
    }

    fun loginWithPhone(
        phoneNumber: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ): Result<Unit> {
        return try {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error en l'enviament del codi: ${e.message}", AuthErrorType.UNKNOWN_ERROR)
        }
    }

    suspend fun verifyCode(verificationId: String, code: String): Result<FirebaseUser> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = firebaseAuth.signInWithCredential(credential).await()
            Result.Success(result.user ?: throw Exception("Usuari no trobat"))
        } catch (e: Exception) {
            Result.Error("Error en la verificació del codi: ${e.message}", AuthErrorType.INVALID_CODE)
        }
    }

    fun isUserLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}