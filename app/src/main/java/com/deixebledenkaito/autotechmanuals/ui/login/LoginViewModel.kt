package com.deixebledenkaito.autotechmanuals.ui.login

import com.deixebledenkaito.autotechmanuals.data.network.auth.Result
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthErrorType

import com.google.firebase.auth.FirebaseUser
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthService


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

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading // Indica que s'està carregant
            when (val result = authService.login(email, password)) {
                is Result.Success -> {
                    _loginState.value = LoginState.Success(result.data) // Inici de sessió exitós
                }
                is Result.Error -> {
                    _loginState.value = LoginState.Error(result.message, result.type) // Error
                }
            }
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

sealed class LoginState {
    object Idle : LoginState() // Estat inicial
    object Loading : LoginState() // S'està carregant
    data class Success(val user: FirebaseUser?) : LoginState() // Inici de sessió exitós
    data class Error(val message: String, val type: AuthErrorType) : LoginState() // Error
}