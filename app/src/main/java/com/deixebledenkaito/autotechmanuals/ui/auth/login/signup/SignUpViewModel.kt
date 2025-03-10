package com.deixebledenkaito.autotechmanuals.ui.auth.login.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthService
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// ViewModel per gestionar la lògica de negoci del registre
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirebaseDataBaseService // Injecta el servei de Firestore
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Funció per registrar l'usuari amb un email, contrasenya, nom i descripció
    fun register(email: String, password: String, name: String, description: String, navigateToHome: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true // Activa l'indicador de càrrega
            try {
                // Registrar l'usuari a Firebase Auth
                val authResult = withContext(Dispatchers.IO) {
                    authService.register(email, password)
                }

                if (authResult != null) {
                    // Obtenir l'ID de l'usuari registrat
                    val userId = authResult.user?.uid ?: throw Exception("No s'ha pogut obtenir l'ID de l'usuari")

                    // Crear un objecte User amb les dades proporcionades
                    val userData = User(
                        id = userId,
                        name = name,
                        email = email,
                        profileImageUrl = "", // Pots deixar-ho buit o assignar una imatge per defecte
                        description = description,
                        stars = 0
                    )

                    // Guardar les dades de l'usuari a Firestore
                    val isSaved = firestoreService.saveUserData(userData)

                    if (isSaved) {
                        Log.d("SignUpViewModel", "Usuari registrat i dades guardades correctament")
                        navigateToHome() // Navega a la pantalla de home
                    } else {
                        Log.e("SignUpViewModel", "Error guardant les dades de l'usuari")
                    }
                }
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Error en el registre: ${e.message}")
            } finally {
                _isLoading.value = false // Desactiva l'indicador de càrrega
            }
        }
    }
}