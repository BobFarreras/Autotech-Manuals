package com.deixebledenkaito.autotechmanuals.ui.auth.login



import com.google.firebase.auth.FirebaseUser
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.service.AuthService
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

// ui/viewmodel/LoginViewModel.kt
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _verificationId = MutableStateFlow<String?>(null)


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            when (val result = authService.login(email, password)) {
                is Result.Success -> {
                    _loginState.value = LoginState.Success(result.data)
                }
                is Result.Error -> {
                    _loginState.value = LoginState.Error(result.message, result.type)
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
            when (val result = authService.loginWithPhone(phoneNumber, callback)) {
                is Result.Success -> {
                    // Codi enviat correctament
                }
                is Result.Error -> {
                    _loginState.value = LoginState.Error(result.message, result.type)
                }
            }
            _isLoading.value = false
        }
    }

    fun verifyCode(code: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _verificationId.value?.let { verificationId ->
                when (val result = authService.verifyCode(verificationId, code)) {
                    is Result.Success -> {
                        onSuccessVerification()
                        _loginState.value = LoginState.Success(result.data)
                    }
                    is Result.Error -> {
                        _loginState.value = LoginState.Error(result.message, result.type)
                    }
                }
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