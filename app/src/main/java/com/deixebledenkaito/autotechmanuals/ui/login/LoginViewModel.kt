package com.deixebledenkaito.autotechmanuals.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthService
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    fun login(user: String, password: String, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = withContext(Dispatchers.IO) {
                authService.login(user, password)
            }
            if (result != null) {
                navigateToDetail()
            }
            _isLoading.value = false
        }
    }

    fun startPhoneNumberVerification(
        phoneNumber: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                authService.loginWithPhone(phoneNumber, callback)
            }
            _isLoading.value = false
        }
    }

    fun verifyCode(code: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = withContext(Dispatchers.IO) {
                _verificationId.value?.let { verificationId ->
                    authService.verifyCode(verificationId, code)
                }
            }
            if (result != null) {
                onSuccessVerification()
            }
            _isLoading.value = false
        }
    }

    fun setVerificationId(verificationId: String) {
        _verificationId.value = verificationId
    }
}