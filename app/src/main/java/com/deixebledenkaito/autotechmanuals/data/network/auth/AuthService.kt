package com.deixebledenkaito.autotechmanuals.data.network.auth

import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthService @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    suspend fun login(user: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(user, password).await()
            Result.Success(result.user) // Retorna un resultat exitós
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Credencials incorrectes", AuthErrorType.INVALID_CREDENTIALS) // Retorna un error
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error("Usuari no trobat", AuthErrorType.USER_NOT_FOUND) // Retorna un error
        } catch (e: Exception) {
            Result.Error("Error desconegut: ${e.message}", AuthErrorType.UNKNOWN_ERROR) // Retorna un error
        }
    }

    // Registre amb correu i contrasenya
    suspend fun register(email: String, password: String): AuthResult? {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    continuation.resume(authResult)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    // Verifica si l'usuari està autenticat
    fun isUserLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Tanca la sessió de l'usuari
    fun logout() {
        firebaseAuth.signOut()
    }

    // Inici de sessió amb telèfon
    suspend fun loginWithPhone(
        phoneNumber: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        return suspendCancellableCoroutine { continuation ->
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            continuation.resume(Unit) // Indica que l'operació s'ha iniciat correctament
        }
    }

    // Verifica el codi rebut per SMS
    suspend fun verifyCode(verificationId: String, code: String): FirebaseUser? {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        return completeRegisterWithPhone(credential)
    }

    // Completa el registre amb les credencials del telèfon
    private suspend fun completeRegisterWithPhone(credential: PhoneAuthCredential): FirebaseUser? {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    continuation.resume(it.user)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }


}
