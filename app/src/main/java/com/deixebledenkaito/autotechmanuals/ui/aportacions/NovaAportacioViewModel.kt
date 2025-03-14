package com.deixebledenkaito.autotechmanuals.ui.aportacions

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.deixebledenkaito.autotechmanuals.data.service.AportacioService
import com.deixebledenkaito.autotechmanuals.data.service.ManualService
import com.deixebledenkaito.autotechmanuals.data.service.ModelService
import com.deixebledenkaito.autotechmanuals.data.service.UserService
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result


@HiltViewModel
class NovaAportacioViewModel @Inject constructor(
    private val userService: UserService,
    private val manualService: ManualService,
    private val modelService: ModelService,
    private val aportacioService: AportacioService,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _manuals = MutableStateFlow<List<Manuals>>(emptyList())
    val manuals: StateFlow<List<Manuals>> get() = _manuals

    private val _models = MutableStateFlow<List<Model>>(emptyList())
    val models: StateFlow<List<Model>> get() = _models

    private val _notificacio = MutableStateFlow("")
    val notificacio: StateFlow<String> get() = _notificacio

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        carregarManuals()
    }

    // Carrega tots els manuals
    private fun carregarManuals() {
        viewModelScope.launch {
            _isLoading.value = true
            _notificacio.value = ""
            try {
                when (val result = manualService.totsElsManuals()) {
                    is Result.Success -> _manuals.value = result.data
                    is Result.Error -> _notificacio.value = "Error carregant manuals: ${result.message}"
                }
            } catch (e: Exception) {
                _notificacio.value = "Error inesperat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carrega els models d'un manual específic
    fun carregarModels(manualId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _notificacio.value = ""
            try {
                when (val result = modelService.getModelsForManual(manualId)) {
                    is Result.Success -> _models.value = result.data
                    is Result.Error -> _notificacio.value = "Error carregant models: ${result.message}"
                }
            } catch (e: Exception) {
                _notificacio.value = "Error inesperat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Guarda una nova aportació
    fun guardarAportacio(
        manual: String?,
        model: String?,
        title: String,
        descripcio: String,
        imatges: List<Uri>,
        pdfUris: List<Uri>,
        videoUris: List<Uri>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _notificacio.value = ""

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                _notificacio.value = "No s'ha trobat l'ID de l'usuari"
                _isLoading.value = false
                return@launch
            }

            // Obtenir les dades de l'usuari
            val user = when (val result = userService.getUser()) {
                is Result.Success -> result.data
                is Result.Error -> {
                    _notificacio.value = "Error obtenint dades de l'usuari: ${result.message}"
                    _isLoading.value = false
                    return@launch
                }
            }

            val userName = user?.name ?: "Usuari desconegut"

            // Generar l'ID de l'aportació
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val dateTime = dateFormat.format(Date())
            val aportacioId = "${dateTime}_${title.replace(" ", "_")}"

            // Obtenir la data i l'hora actuals
            val data = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            try {
                // Pujar imatges
                val imatgeUrls = pujarFitxers(imatges, userId, aportacioId, "imatges")

                // Pujar PDFs
                val pdfUrls = pujarFitxers(pdfUris, userId, aportacioId, "pdfs")

                // Pujar vídeos
                val videoUrls = pujarFitxers(videoUris, userId, aportacioId, "videos")

                // Crear l'objecte AportacioUser
                val aportacio = AportacioUser(
                    id = aportacioId,
                    model = model ?: "",
                    manual = manual ?: "",
                    title = title,
                    descripcio = descripcio,
                    imageUrls = imatgeUrls.joinToString(","),
                    likes = 0,
                    noLikes = 0,
                    usageCount = 0,
                    pdfUrls = pdfUrls.joinToString(","),
                    videoUrls = videoUrls.joinToString(","),
                    data = data,
                    hora = hora,
                    user = userId,
                    userName = userName,
                    usersWhoLiked = mutableListOf(),
                    usersWhoDisliked = mutableListOf()
                )

                // Guardar l'aportació a Firestore
                when (val result = aportacioService.addAportacio(userId, aportacio)) {
                    is Result.Success -> {
                        _notificacio.value = "Aportació guardada correctament"
                        Log.d("NovaAportacioViewModel", "Aportació guardada correctament")
                    }
                    is Result.Error -> {
                        _notificacio.value = "Error guardant l'aportació: ${result.message}"
                        Log.e("NovaAportacioViewModel", "Error guardant l'aportació", result.exception)
                    }
                }
            } catch (e: Exception) {
                _notificacio.value = "Error inesperat: ${e.message}"
                Log.e("NovaAportacioViewModel", "Error inesperat", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funció per pujar fitxers (imatges, PDFs, vídeos) a Firebase Storage
    private suspend fun pujarFitxers(fitxers: List<Uri>, userId: String, aportacioId: String, tipus: String): List<String> {
        return fitxers.mapNotNull { uri ->
            try {
                val fitxerName = UUID.randomUUID().toString()
                val fitxerRef = storage.reference.child("usuaris/$userId/aportacions/$aportacioId/$tipus/$fitxerName")
                val uploadTask = fitxerRef.putFile(uri).await()
                uploadTask.storage.downloadUrl.await().toString()
            } catch (e: Exception) {
                Log.e("NovaAportacioViewModel", "Error pujant fitxer: ${e.message}")
                null
            }
        }
    }
}