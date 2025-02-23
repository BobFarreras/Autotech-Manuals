package com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ModelDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
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
                // Carregar el model
                val model = firestore.collection("manuals")
                    .document(manualId)
                    .collection("models")
                    .document(modelId)
                    .get()
                    .await()
                    .toObject(Model::class.java)
                _model.value = model

                // Carregar les aportacions
                val aportacions = firestore.collection("manuals")
                    .document(manualId)
                    .collection("models")
                    .document(modelId)
                    .collection("aportacions")
                    .get()
                    .await()
                    .toObjects(AportacioUser::class.java)
                _aportacions.value = aportacions
            } catch (e: Exception) {
                _error.value = "Error carregant dades: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funció per eliminar una aportació
    suspend fun eliminarAportacio(aportacio: AportacioUser): Boolean {
        return try {
            firestore.collection("manuals")
                .document(aportacio.manual)
                .collection("models")
                .document(aportacio.model)
                .collection("aportacions")
                .document(aportacio.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            _error.value = "Error eliminant aportació: ${e.message}"
            false
        }
    }
}