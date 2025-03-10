package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.sharedViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService

import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) : ViewModel() {

    // Estat per a l'ID de l'usuari
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> get() = _userId

    // Estat per a les rutes guardades
    private val _rutesGuardades = MutableStateFlow<List<RutaGuardada>>(emptyList())
    val rutesGuardades: StateFlow<List<RutaGuardada>> get() = _rutesGuardades

    // Estat per als missatges (Snackbar)
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> get() = _snackbarMessage

    // Funció per guardar una ruta
    fun guardarRuta(ruta: RutaGuardada) {
        viewModelScope.launch {
            val userId = firebaseDataBaseService.getUser()
            if (userId != null) {
                val result = firebaseDataBaseService.guardarRuta(userId.id, ruta)
                if (result) {
                    _snackbarMessage.value = "Ruta guardada correctament"
                    Log.d("SharedViewModel", "Ruta guardada correctament")
                } else {
                    _snackbarMessage.value = "Error guardant la ruta"
                    Log.e("SharedViewModel", "Error guardant la ruta")
                }
            } else {
                _snackbarMessage.value = "No s'ha trobat l'ID de l'usuari"
                Log.e("SharedViewModel", "No s'ha trobat l'ID de l'usuari")
            }
        }
    }

    // Funció per carregar les rutes guardades
    fun carregarRutesGuardades() {
        viewModelScope.launch {
            val userId = firebaseDataBaseService.getUser()
            if (userId != null) {
                _rutesGuardades.value = firebaseDataBaseService.obtenirRutesGuardades(userId.id)
            }
        }
    }

    // Funció per netejar el missatge del Snackbar
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}