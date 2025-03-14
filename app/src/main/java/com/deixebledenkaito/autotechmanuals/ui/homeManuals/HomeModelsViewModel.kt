package com.deixebledenkaito.autotechmanuals.ui.homeManuals


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.service.ManualService
import com.deixebledenkaito.autotechmanuals.data.service.ModelService
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.domain.User
import com.deixebledenkaito.autotechmanuals.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel


@HiltViewModel
class HomeModelsViewModel @Inject constructor(
    private val manualService: ManualService,
    private val modelService: ModelService // Injecta el ModelService
) : ViewModel() {

    // Estats i flows
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


    // Carrega els models i el manual per un manual específic
    fun loadModels(manualName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Neteja errors previs

            // Carrega el manual
            when (val manualResult = manualService.getManualByName(manualName)) {
                is Result.Success -> {
                    _manual.value = manualResult.data // Actualitza el manual
                }
                is Result.Error -> {
                    _error.value = "Error carregant manual: ${manualResult.message}"
                }
            }

            // Carrega els models
            when (val modelsResult = modelService.getModelsForManual(manualName)) {
                is Result.Success -> {
                    _models.value = modelsResult.data // Actualitza els models
                }
                is Result.Error -> {
                    _error.value = "Error carregant models: ${modelsResult.message}"
                }
            }

            _isLoading.value = false
        }
    }
}