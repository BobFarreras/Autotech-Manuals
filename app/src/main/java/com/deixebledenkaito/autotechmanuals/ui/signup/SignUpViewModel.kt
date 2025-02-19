package com.deixebledenkaito.autotechmanuals.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// ViewModel per gestionar la lògica de negoci del registre
@HiltViewModel
class SignUpViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    // Estat per controlar si la càrrega està activa (per mostrar un spinner, per exemple)
    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Funció per registrar l'usuari amb un email i contrasenya
    fun register(email: String, password: String, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true // Activa l'indicador de càrrega
            try {
                val result = withContext(Dispatchers.IO) {
                    authService.register(email, password) // Crida al servei d'autenticació
                }
                if (result != null) {
                    navigateToDetail() // Navega a la pantalla de detall si el registre té èxit
                } else {
                    Log.i("SignUpViewModel", "Error en el registre")
                }
            } catch (e: Exception) {
                Log.i("SignUpViewModel", e.message.orEmpty()) // Registra qualsevol error
            }
            _isLoading.value = false // Desactiva l'indicador de càrrega
        }
    }
}
