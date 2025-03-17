package com.deixebledenkaito.autotechmanuals.ui.Profile

import android.net.Uri
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.utils.Result
import com.deixebledenkaito.autotechmanuals.data.repository.UserRepository
import com.deixebledenkaito.autotechmanuals.data.service.AportacioService
import com.deixebledenkaito.autotechmanuals.data.service.UserService
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val aportacioService: AportacioService,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _userAportacions = MutableStateFlow<List<AportacioUser>>(emptyList())
    val userAportacions: StateFlow<List<AportacioUser>> get() = _userAportacions

    // Estat per controlar la visibilitat del Dialog de càrrega
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Estat per als missatges d'error
    private val _errorMessage = MutableStateFlow<String?>(null)

    // Estat per als errors
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Carrega les dades de l'usuari
    fun loadUser() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUser()
                _user.value = currentUser
                currentUser?.let { user ->
                    val aportacions = aportacioService.getAportacionsByUser(user.id)
                    _userAportacions.value = aportacions
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error carregant l'usuari: ${e.message}"
            }
        }
    }

    // Funció per eliminar una aportació
    fun eliminarAportacio(userId: String, aportacio: AportacioUser) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val eliminada = aportacioService.eliminarAportacio(userId, aportacio)
                if (eliminada) {
                    // Actualitza la llista d'aportacions després de l'eliminació
                    loadUserAportacions(userId)
                } else {
                    _error.value = "Error eliminant l'aportació"
                }
            } catch (e: Exception) {
                _error.value = "Error eliminant l'aportació: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Actualitza la imatge de perfil
    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Neteja errors previs
            try {
                when (val imageUrlResult = userService.uploadAndDownloadImage(imageUri, auth.currentUser?.uid ?: "")) {
                    is Result.Success -> {
                        val imageUrl = imageUrlResult.data
                        val success = userService.updateUserProfileImage(
                            userId = auth.currentUser?.uid ?: "",
                            imageUrl = imageUrl
                        )
                        if (success) {
                            _user.value = _user.value?.copy(profileImageUrl = imageUrl)
                        } else {
                            _errorMessage.value = "Error actualitzant la imatge de perfil"
                        }
                    }
                    is Result.Error -> {
                        _errorMessage.value = "Error pujant la imatge: ${imageUrlResult.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carrega les aportacions de l'usuari
    fun loadUserAportacions(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val aportacions = aportacioService.getAportacionsByUser(userId)
                Log.d("AportacioRepository", "Aportacions carregades: ${aportacions.size}")
                _userAportacions.value = aportacions
            } catch (e: Exception) {
                _error.value = "Error inesperat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}