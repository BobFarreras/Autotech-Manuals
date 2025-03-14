package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.service.ModelService
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


}