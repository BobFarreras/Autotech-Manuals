package com.deixebledenkaito.autotechmanuals.ui.home


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthService
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.User

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService,
    private val authService: AuthService
) : ViewModel() {

    private val _manuals = MutableStateFlow<List<Manuals>>(emptyList())
    val manuals: StateFlow<List<Manuals>> get() = _manuals

    private val _topManuals = MutableStateFlow<List<Manuals>>(emptyList())
    val topManuals: StateFlow<List<Manuals>> get() = _topManuals

    private val _lastManual = MutableStateFlow<Manuals?>(null)
    val lastManual: StateFlow<Manuals?> get() = _lastManual

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    // Funció per carregar totes les dades necessàries
    fun loadAllData() {
        viewModelScope.launch {
            loadManuals()
            loadTopManuals()
            loadLastManual()
            loadUser()
        }
    }

    fun loadManuals() {
        viewModelScope.launch {
            _manuals.value = firebaseDataBaseService.totsElsManuals()
        }
    }

    fun loadTopManuals() {
        viewModelScope.launch {
            val topManualIds = firebaseDataBaseService.getTopManuals()
            _topManuals.value = topManualIds.mapNotNull { id ->
                firebaseDataBaseService.getManualByName(id)
            }
        }
    }

    fun loadLastManual() {
        viewModelScope.launch {
            val lastManualName = firebaseDataBaseService.getLastUsedManual()
            if (lastManualName != null) {
                _lastManual.value = firebaseDataBaseService.getManualByName(lastManualName)
            }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            _user.value = firebaseDataBaseService.getUser() ?: User(
                id = "unknown",
                name = "Usuari",
                profileImageUrl = ""
            )
        }
    }

    // Funció per tancar sessió
    fun logout(navigationToLogin: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authService.logout() // Tanca la sessió de Firebase
            withContext(Dispatchers.Main) {
                navigationToLogin() // Executa el callback per navegar a Login
            }
        }
    }

    // Funció per incrementar l'ús d'un manual
    fun incrementManualUsage(manualId: String) {
        viewModelScope.launch {
            try {
                firebaseDataBaseService.incrementManualUsage(manualId)
                loadTopManuals() // Recarregar la llista de manuals destacats
            } catch (e: Exception) {
                // Manejar l'error
                println("Error incrementant l'ús del manual: ${e.message}")
            }
        }
    }

    // Funció per actualitzar l'últim manual utilitzat
    fun updateLastUsedManual(manualName: String) {
        viewModelScope.launch {
            firebaseDataBaseService.updateLastUsedManual(manualName)
            loadLastManual() // Recarregar l'últim manual utilitzat
        }
    }

    // Funció per afegir un nou manual
    fun addManual(nom: String, descripcio: String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                // Puja la imatge a Firebase Storage i obté l'URL
                val imageUrl = firebaseDataBaseService.uploadAndDownloadImage(imageUri)

                // Afegeix el manual a Firestore
                val success = firebaseDataBaseService.afegirManual(nom, descripcio, imageUrl, 0)

                if (success) {
                    loadManuals() // Recarregar la llista de manuals
                }
            } catch (e: Exception) {
                // Manejar l'error
                println("Error afegint el manual: ${e.message}")
            }
        }
    }
}