package com.deixebledenkaito.autotechmanuals.ui.home



import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.auth.AuthService
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
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
    // Càrrega de totes les dades
    fun loadAllData() {
        viewModelScope.launch {
            loadManuals()
            loadTopManuals()
            loadLastManual()
            loadUser()

        }
    }

    // Càrrega dels manuals
    private fun loadManuals() {
        viewModelScope.launch {
            if (!firebaseDataBaseService.isCacheValid()) {
                _manuals.value = firebaseDataBaseService.totsElsManuals()
                Log.d("HomeViewModel", "Manuals carregats des de: ${if (firebaseDataBaseService.isCacheValid()) "Cache" else "Firebase"}")
            }

        }
    }

    // Càrrega dels manuals destacats
    private fun loadTopManuals() {
        viewModelScope.launch {
            val topManualIds = firebaseDataBaseService.getTopManuals()
            _topManuals.value = topManualIds.mapNotNull { id ->
                firebaseDataBaseService.getManualByName(id) // Assigna la imatge local aquí
            }
            Log.d("HomeViewModel", "Top manuals carregats des de: ${if (firebaseDataBaseService.isCacheValid()) "Cache" else "Firebase"}")
        }
    }

    // Càrrega de l'últim manual utilitzat
    private fun loadLastManual() {
        viewModelScope.launch {
            val lastManualName = firebaseDataBaseService.getLastUsedManual()
            if (lastManualName != null) {
                _lastManual.value = firebaseDataBaseService.getManualByName(lastManualName) // Assigna la imatge local aquí
            }
        }
    }

    // Actualització de l'últim manual utilitzat
    fun updateLastUsedManual(manualName: String) {
        viewModelScope.launch {
            val success = firebaseDataBaseService.updateLastUsedManual(manualName)
            if (success) {
                loadLastManual()
            }
        }
    }

    // Càrrega de l'usuari
    fun loadUser() {
        viewModelScope.launch {
            _user.value = firebaseDataBaseService.getUser()
        }
    }

    // Tancament de sessió
    fun logout(navigationToLogin: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authService.logout()
            withContext(Dispatchers.Main) {
                navigationToLogin()
            }
        }
    }

    // Increment de l'ús del manual
    fun incrementManualUsage(manualId: String) {
        viewModelScope.launch {
            firebaseDataBaseService.incrementManualUsage(manualId)
            loadTopManuals()
        }
    }


}



    // Funció per afegir un nou manual
//    fun addManual(nom: String, descripcio: String, imageUri: Uri) {
//        viewModelScope.launch {
//            try {
//                // Puja la imatge a Firebase Storage i obté l'URL
//                val imageUrl = firebaseDataBaseService.uploadAndDownloadImage(imageUri)
//
//                // Afegeix el manual a Firestore
//                val success = firebaseDataBaseService.afegirManual(nom, descripcio, imageUrl, 0)
//
//                if (success) {
//                    loadManuals() // Recarregar la llista de manuals
//                }
//            } catch (e: Exception) {
//                // Manejar l'error
//                println("Error afegint el manual: ${e.message}")
//            }
//        }
//    }




