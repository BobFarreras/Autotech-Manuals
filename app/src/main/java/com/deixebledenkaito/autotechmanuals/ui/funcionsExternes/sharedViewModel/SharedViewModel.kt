package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.sharedViewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.deixebledenkaito.autotechmanuals.data.service.RutaGuardadaService
import com.deixebledenkaito.autotechmanuals.data.service.UserService
import com.deixebledenkaito.autotechmanuals.utils.Result
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userService: UserService, // Servei per obtenir l'ID de l'usuari
    private val rutaGuardadaService: RutaGuardadaService
) : ViewModel() {

    private val _rutesGuardades = MutableStateFlow<List<RutaGuardada>>(emptyList())
    val rutesGuardades: StateFlow<List<RutaGuardada>> get() = _rutesGuardades

    private val _rutesCarregades = MutableStateFlow(false)


    private val _snackbarMessage = MutableStateFlow<String?>(null)


    // Estat per al diàleg de missatges
    private val _showMessageDialog = MutableStateFlow(false)
    val showMessageDialog: StateFlow<Boolean> get() = _showMessageDialog

    private val _messageDialogText = MutableStateFlow("")
    val messageDialogText: StateFlow<String> get() = _messageDialogText

    // Funció per carregar les rutes guardades
    fun carregarRutesGuardades() {
//        if (_rutesCarregades.value) {
//            Log.d("SharedViewModel", "Les rutes ja s'han carregat prèviament.")
//            return
//        }

        viewModelScope.launch {
            try {
                when (val userResult = userService.getUser()) {
                    is Result.Success -> {
                        val userId =
                            userResult.data?.id // Assegura't que l'objecte d'usuari té una propietat `id`
                        Log.d("SharedViewModel", "Carregant rutes guardades per l'usuari: $userId")
                        when (val result = rutaGuardadaService.obtenirRutesGuardades(userId!!)) {
                            is Result.Success -> {
                                Log.d("SharedViewModel", "Rutes guardades carregades: ${result.data.size}")
                                _rutesGuardades.value = result.data
                                _rutesCarregades.value = true
                            }
                            is Result.Error -> {
                                Log.e("SharedViewModel", "Error carregant les rutes: ${result.message}")
                                _snackbarMessage.value = "Error carregant les rutes: ${result.message}"
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.e("SharedViewModel", "Error obtenint l'usuari: ${userResult.message}")
                        _snackbarMessage.value = "Error obtenint l'usuari: ${userResult.message}"
                    }
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error inesperat: ${e.message}")
                _snackbarMessage.value = "Error inesperat: ${e.message}"
            }
        }
    }

    // Funció per mostrar el diàleg de missatges
    fun showMessageDialog(message: String) {
        _messageDialogText.value = message
        _showMessageDialog.value = true
    }

    // Funció per amagar el diàleg de missatges
    fun hideMessageDialog() {
        _showMessageDialog.value = false
    }
    // Funció per verificar si una ruta està guardada pel seu contingut
    fun isRutaGuardada(rutaContent: String): StateFlow<Boolean> {
        val result = MutableStateFlow(false)
        viewModelScope.launch {
            when (val userResult = userService.getUser()) {
                is Result.Success -> {
                    val userId = userResult.data?.id
                    if (userId != null) {
                        when (val rutaResult = rutaGuardadaService.isRutaGuardada(userId, rutaContent)) {
                            is Result.Success -> result.value = rutaResult.data
                            is Result.Error -> Log.e("SharedViewModel", "Error verificant la ruta: ${rutaResult.message}")
                        }
                    }
                }
                is Result.Error -> Log.e("SharedViewModel", "Error obtenint l'usuari: ${userResult.message}")
            }
        }
        return result
    }
    fun guardarRuta(ruta: RutaGuardada) {
        viewModelScope.launch {
            try {
                when (val userResult = userService.getUser()) {
                    is Result.Success -> {
                        val userId = userResult.data?.id
                        if (userId != null) {
                            // Verifica si la ruta ja està guardada pel seu contingut
                            when (val rutaResult = rutaGuardadaService.isRutaGuardada(userId, ruta.ruta)) {
                                is Result.Success -> {
                                    if (!rutaResult.data) {
                                        // Guarda la ruta si no està guardada
                                        when (val saveResult = rutaGuardadaService.guardarRuta(userId, ruta)) {
                                            is Result.Success -> {
                                                Log.d("SharedViewModel", "Ruta guardada correctament")
                                                showMessageDialog("Ruta guardada correctament")
                                            }
                                            is Result.Error -> {
                                                Log.e("SharedViewModel", "Error guardant la ruta: ${saveResult.message}")
                                                showMessageDialog("Error guardant la ruta: ${saveResult.message}")
                                            }
                                        }
                                    } else {
                                        Log.d("SharedViewModel", "La ruta ja està guardada")
                                        showMessageDialog("Aquesta ruta ja està guardada")
                                    }
                                }
                                is Result.Error -> {
                                    Log.e("SharedViewModel", "Error verificant la ruta: ${rutaResult.message}")
                                    showMessageDialog("Error verificant la ruta: ${rutaResult.message}")
                                }
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.e("SharedViewModel", "Error obtenint l'usuari: ${userResult.message}")
                        showMessageDialog("Error obtenint l'usuari: ${userResult.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error inesperat: ${e.message}")
                showMessageDialog("Error inesperat: ${e.message}")
            }
        }
    }



}