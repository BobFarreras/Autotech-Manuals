package com.deixebledenkaito.autotechmanuals.ui.homeManuals


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser

import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.domain.User
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await

@HiltViewModel
class HomeModelsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

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

    // Estat per a les aportacions de cada model
    private val _aportacionsPerModel = MutableStateFlow<Map<String, List<AportacioUser>>>(emptyMap())
    val aportacionsPerModel: StateFlow<Map<String, List<AportacioUser>>> get() = _aportacionsPerModel

    fun loadModels(manualName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val models = firestore.collection("manuals")
                    .document(manualName)
                    .collection("models")
                    .get()
                    .await()
                    .toObjects(Model::class.java)
                _models.value = models

                // Carregar aportacions per a cada model
                models.forEach { model ->
                    loadAportacionsForModel(model.id)
                }
            } catch (e: Exception) {
                _error.value = "Error carregant models: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadManual(manualName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val manual = firestore.collection("manuals")
                    .document(manualName)
                    .get()
                    .await()
                    .toObject(Manuals::class.java)
                _manual.value = manual
            } catch (e: Exception) {
                _error.value = "Error carregant manual: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadAportacionsForModel(modelId: String) {
        viewModelScope.launch {
            try {
                val aportacions = firestore.collection("aportacions")
                    .whereEqualTo("modelId", modelId)
                    .get()
                    .await()
                    .toObjects(AportacioUser::class.java)

                // Actualitzar el mapa d'aportacions per model
                _aportacionsPerModel.value += (modelId to aportacions)
            } catch (e: Exception) {
                _error.value = "Error carregant aportacions: ${e.message}"
            }
        }
    }
}