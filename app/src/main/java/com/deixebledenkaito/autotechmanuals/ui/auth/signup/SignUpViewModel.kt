package com.deixebledenkaito.autotechmanuals.ui.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.service.AuthService
import com.deixebledenkaito.autotechmanuals.data.service.UserService
import com.deixebledenkaito.autotechmanuals.domain.User
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
// ui/viewmodel/SignUpViewModel.kt
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val userService: UserService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun register(email: String, password: String, name: String, description: String, navigateToHome: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _signUpState.value = SignUpState.Loading

            when (val authResult = authService.register(email, password)) {
                is Result.Success -> {
                    val userId = authResult.data.uid
                    val user = User(
                        id = userId,
                        name = name,
                        email = email,
                        profileImageUrl = "",
                        description = description,
                        stars = 0
                    )

                    when (val saveResult = userService.saveUserData(user)) {
                        is Result.Success -> {
                            _signUpState.value = SignUpState.Success
                            navigateToHome()
                        }
                        is Result.Error -> {
                            _signUpState.value = SignUpState.Error(saveResult.message, saveResult.type)
                        }
                    }
                }
                is Result.Error -> {
                    _signUpState.value = SignUpState.Error(authResult.message, authResult.type)
                }
            }

            _isLoading.value = false
        }
    }
}

sealed class SignUpState {
    object Idle : SignUpState() // Estat inicial
    object Loading : SignUpState() // S'està carregant
    object Success : SignUpState() // Registre exitós
    data class Error(val message: String, val type: AuthErrorType) : SignUpState() // Error
}