package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnErrors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.utils.Result
import com.deixebledenkaito.autotechmanuals.data.service.ErrorService
import com.deixebledenkaito.autotechmanuals.domain.ErrorsDelModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ErrorsDelModelViewModel @Inject constructor(
    private val errorService: ErrorService

) : ViewModel() {

    private val _error = MutableStateFlow<ErrorsDelModel?>(null)
    val error: StateFlow<ErrorsDelModel?> get() = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun buscarErrorPerNumero(manualId: String, modelId: String, numero: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                when (val result = errorService.buscarErrorPerNumero(manualId, modelId, numero)) {
                    is Result.Success -> {
                        val error = result.data
                        if (error == null) {
                            _errorMessage.value = "No s'ha trobat cap error amb aquest número."
                            _error.value = null
                        } else {
                            _error.value = error
                        }
                    }
                    is Result.Error -> {
                        _errorMessage.value = "Error cercant l'error: ${result.message}"
                        _error.value = null
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Excepció inesperada: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}