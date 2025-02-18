package com.deixebledenkaito.autotechmanuals.data.network.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthService  @Inject constructor(private val firebaseAuth: FirebaseAuth) {
    suspend fun login(user:String, password:String): FirebaseUser? {
        return firebaseAuth.signInWithEmailAndPassword(user,password).await().user
    }
    suspend fun register(email: String, password: String)  : FirebaseUser?{
        //return firebaseAuth.createUserWithEmailAndPassword(email,password).await().user
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val user = it.user
                    cancellableContinuation.resume(user)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }
    }
    fun isUserLogged(): Boolean {
        return getCurrentUser()!= null
    }
    private fun getCurrentUser() = firebaseAuth.currentUser
    fun logout() {
        firebaseAuth.signOut()
    }

    fun loginWithPhone(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {

        //firebaseAuth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("34 697974872", "123456")

        val options = PhoneAuthOptions
            .newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    suspend fun verifyCode(verifcationCode: String, phoneCode: String) : FirebaseUser? {
        val credentials = PhoneAuthProvider.getCredential(verifcationCode, phoneCode)
        return completeRegisterWithPhone(credentials)

    }

    suspend fun completeRegisterWithPhone(credential: PhoneAuthCredential) : FirebaseUser?{

        return suspendCancellableCoroutine {cancellableContinuation ->
            firebaseAuth.signInWithCredential(credential).addOnSuccessListener { cancellableContinuation.resume(it.user)
            }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }
    }


}