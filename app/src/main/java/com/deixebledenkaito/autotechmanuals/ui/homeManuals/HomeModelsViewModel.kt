package com.deixebledenkaito.autotechmanuals.ui.homeManuals


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.Model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class HomeModelsViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) : ViewModel() {

    private val _models = MutableStateFlow<List<Model>>(emptyList())
    val models: StateFlow<List<Model>> get() = _models

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun loadModels(manualName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _models.value = firebaseDataBaseService.getModelsForManual(manualName)
            } catch (e: Exception) {
                _error.value = "Error carregant els models: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}