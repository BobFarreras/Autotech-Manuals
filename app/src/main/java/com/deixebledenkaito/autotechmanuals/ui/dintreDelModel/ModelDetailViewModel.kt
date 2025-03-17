package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.deixebledenkaito.autotechmanuals.data.service.ModelService
import com.deixebledenkaito.autotechmanuals.data.service.UserService
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.Model
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.deixebledenkaito.autotechmanuals.utils.Result

import javax.inject.Inject

@HiltViewModel
class ModelDetailViewModel @Inject constructor(
    private val modelService: ModelService,
    private val userService: UserService,


) : ViewModel() {

    // Estat per al model
    private val _model = MutableStateFlow<Model?>(null)
    val model: StateFlow<Model?> get() = _model

    // Estat per a les aportacions
    private val _aportacions = MutableStateFlow<List<AportacioUser>>(emptyList())
    val aportacions: StateFlow<List<AportacioUser>> get() = _aportacions

    // Estat per indicar si s'està carregant
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Estat per als errors
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Carrega el model i les aportacions
    fun loadModelAndAportacions(manualId: String, modelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Carrega el model
                when (val modelResult = modelService.getModelById(manualId, modelId)) {
                    is Result.Success -> {
                        _model.value = modelResult.data
                        // Carrega les aportacions del model
                        when (val aportacionsResult =
                            modelService.getAportacionsForModel(manualId, modelId)) {
                            is Result.Success -> {
                                _aportacions.value = aportacionsResult.data
                                Log.d(
                                    "ModelDetailViewModel",
                                    "Dades carregades: ${aportacionsResult.data.size} aportacions"
                                )
                            }

                            is Result.Error -> {
                                _error.value =
                                    "Error carregant aportacions: ${aportacionsResult.message}"
                            }
                        }
                    }

                    is Result.Error -> {
                        _error.value = "Error carregant el model: ${modelResult.message}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error inesperat: ${e.message}"
                Log.e("ModelDetailViewModel", "Error inesperat", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getCurrentUserId(): String? {
        return when (val userResult = userService.getUser()) {
            is Result.Success -> userResult.data?.id
            is Result.Error -> {
                _error.value = "Error obtenint usuari: ${userResult.message}"
                null
            }
        }
    }

    // Funció per donar like a una aportació
    fun likeAportacio(aportacioId: String, manualId: String, modelId: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    when (val result = modelService.donarLike(aportacioId, userId, manualId, modelId)) {
                        is Result.Success -> {
                            // Actualitza l'aportació a la llista
                            updateAportacio(aportacioId) { aportacio ->
                                // Afegeix l'usuari a la llista de likes
                                if (!aportacio.usersWhoLiked.contains(userId)) {
                                    aportacio.usersWhoLiked.add(userId)
                                    aportacio.likes++
                                }
                                // Elimina l'usuari de la llista de dislikes si hi és
                                if (aportacio.usersWhoDisliked.contains(userId)) {
                                    aportacio.usersWhoDisliked.remove(userId)
                                    aportacio.noLikes--
                                }
                                aportacio // Retorna l'aportació actualitzada
                            }
                        }
                        is Result.Error -> {
                            _error.value = "Error donant like: ${result.message}"
                        }
                    }
                } else {
                    _error.value = "Usuari no autenticat"
                }
            } catch (e: Exception) {
                _error.value = "Error inesperat: ${e.message}"
            }
        }
    }

    // Funció per donar dislike a una aportació
    fun dislikeAportacio(aportacioId: String, manualId: String, modelId: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    when (val result = modelService.donarDislike(aportacioId, userId,manualId, modelId )) {
                        is Result.Success -> {
                            // Actualitza l'aportació a la llista
                            updateAportacio(aportacioId) { aportacio ->
                                // Afegeix l'usuari a la llista de dislikes
                                if (!aportacio.usersWhoDisliked.contains(userId)) {
                                    aportacio.usersWhoDisliked.add(userId)
                                    aportacio.noLikes++
                                }
                                // Elimina l'usuari de la llista de likes si hi és
                                if (aportacio.usersWhoLiked.contains(userId)) {
                                    aportacio.usersWhoLiked.remove(userId)
                                    aportacio.likes--
                                }
                                aportacio // Retorna l'aportació actualitzada
                            }
                        }
                        is Result.Error -> {
                            _error.value = "Error donant dislike: ${result.message}"
                        }
                    }
                } else {
                    _error.value = "Usuari no autenticat"
                }
            } catch (e: Exception) {
                _error.value = "Error inesperat: ${e.message}"
            }
        }
    }

    private fun updateAportacio(aportacioId: String, update: (AportacioUser) -> AportacioUser) {
        _aportacions.value = _aportacions.value.map { aportacio ->
            if (aportacio.id == aportacioId) {
                val updatedAportacio = update(aportacio.copy())
                Log.d("updateAportacio", "Aportació actualitzada: ${updatedAportacio.id}, likes: ${updatedAportacio.likes}, dislikes: ${updatedAportacio.noLikes}")
                updatedAportacio
            } else {
                aportacio
            }
        }
    }
}