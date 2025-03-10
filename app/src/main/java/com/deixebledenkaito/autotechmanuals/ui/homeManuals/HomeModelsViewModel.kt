package com.deixebledenkaito.autotechmanuals.ui.homeManuals


import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService

import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.domain.User


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel


@HiltViewModel
class HomeModelsViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService // Injecta el servei,


) : ViewModel() {

    // Estats i flows (sense canvis)
    private val _models = MutableStateFlow<List<Model>>(emptyList())
    val models: StateFlow<List<Model>> get() = _models

    private val _manual = MutableStateFlow<Manuals?>(null)
    val manual: StateFlow<Manuals?> get() = _manual

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _model = MutableStateFlow<Model?>(null)
    val model: StateFlow<Model?> get() = _model


    fun loadModels(manualName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val models = firebaseDataBaseService.getModelsForManual(manualName)
                _models.value = models


                Log.d("HomeViewModel", "Models carregats des de: ${if (firebaseDataBaseService.isCacheValid()) "Cache" else "Firebase"}")
            } catch (e: Exception) {
                _error.value = "Error carregant models: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carregar manual utilitzant el servei
    fun loadManual(manualName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val manual = firebaseDataBaseService.getManualByName(manualName)
                _manual.value = manual
            } catch (e: Exception) {
                _error.value = "Error carregant manual: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}