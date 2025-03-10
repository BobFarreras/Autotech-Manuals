package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.Model

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ModelDetailViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) : ViewModel() {

    private val _model = MutableStateFlow<Model?>(null)
    val model: StateFlow<Model?> get() = _model

    private val _aportacions = MutableStateFlow<List<AportacioUser>>(emptyList())
    val aportacions: StateFlow<List<AportacioUser>> get() = _aportacions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Carrega el model i les aportacions
    fun loadModelAndAportacions(manualId: String, modelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (model, aportacions) = firebaseDataBaseService.loadModelAndAportacions(manualId, modelId)
                _model.value = model
                _aportacions.value = aportacions
                Log.d("Aportacions - MOdelDetailViewModel", aportacions.toString())
                // Log per verificar que les dades s'han carregat
                Log.d("ModelDetailViewModel", "Dades carregades: ${aportacions.size} aportacions")
            } catch (e: Exception) {
                _error.value = "Error carregant dades: ${e.message}"
                Log.e("ModelDetailViewModel", "Error carregant dades", e)
            } finally {
                _isLoading.value = false
            }
        }

    }

    // Funció per eliminar una aportació
    suspend fun eliminarAportacio(aportacio: AportacioUser): Boolean {
        return try {
            firebaseDataBaseService.eliminarAportacio(aportacio)
        } catch (e: Exception) {
            _error.value = "Error eliminant aportació: ${e.message}"
            false
        }
    }

    // Funció per actualitzar likes o no likes
    suspend fun updateLikeOrNoLike(aportacio: AportacioUser, userId: String, isLike: Boolean): AportacioUser {
        return try {
            firebaseDataBaseService.updateLikeOrNoLike(aportacio, userId, isLike)
        } catch (e: Exception) {
            _error.value = "Error actualitzant Like/No Like: ${e.message}"
            throw e
        }
    }

}