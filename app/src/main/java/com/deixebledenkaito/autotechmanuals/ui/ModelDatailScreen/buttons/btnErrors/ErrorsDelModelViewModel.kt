package com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen.buttons.btnErrors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.ErrorsDelModel


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ErrorsDelModelViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) : ViewModel() {

    private val _error = MutableStateFlow<ErrorsDelModel?>(null)
    val error: StateFlow<ErrorsDelModel?> get() = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    // Buscar un error pel número
    fun buscarErrorPerNumero(manualId: String, modelId: String, numero: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val error = firebaseDataBaseService.buscarErrorPerNumero(manualId, modelId, numero)
                if (error == null) {
                    _errorMessage.value = "No s'ha trobat cap error amb aquest número."
                    _error.value = null
                } else {
                    _error.value = error
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error cercant l'error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}