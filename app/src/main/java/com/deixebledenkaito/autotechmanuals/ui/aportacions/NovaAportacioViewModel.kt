package com.deixebledenkaito.autotechmanuals.ui.aportacions

import android.content.Context
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope

@HiltViewModel
class NovaAportacioViewModel @Inject constructor(
    private val userService: UserService,
    private val manualService: ManualService,
    private val modelService: ModelService,
    private val aportacioService: AportacioService,


) : ViewModel() {

    // Estats per a la UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _notificacio = MutableStateFlow("")
    val notificacio: StateFlow<String> get() = _notificacio

    private val _manuals = MutableStateFlow<List<Manuals>>(emptyList())
    val manuals: StateFlow<List<Manuals>> get() = _manuals

    private val _models = MutableStateFlow<List<Model>>(emptyList())
    val models: StateFlow<List<Model>> get() = _models

    // Estat per al progrés de pujada
    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> get() = _uploadProgress
    init {
        Log.d("NovaAportacioViewModel", "Inicialitzant ViewModel i carregant manuals")
        carregarManuals()
    }

    // Funció per sanititzar el títol
    private fun sanitizeTitle(title: String): String {
        return title.replace("/", "_")
            .replace(" ", "_")
            .replace("[^a-zA-Z0-9_.-]".toRegex(), "_")
    }

    // Carrega tots els manuals
    private fun carregarManuals() {
        viewModelScope.launch {
            _isLoading.value = true
            _notificacio.value = ""
            try {
                Log.d("NovaAportacioViewModel", "Carregant manuals...")
                when (val result = manualService.totsElsManuals()) {
                    is Result.Success -> {
                        _manuals.value = result.data
                        Log.d("NovaAportacioViewModel", "Manuals carregats: ${result.data.size}")
                    }
                    is Result.Error -> {
                        _notificacio.value = "Error carregant manuals: ${result.message}"
                        Log.e("NovaAportacioViewModel", "Error carregant manuals: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                _notificacio.value = "Error inesperat: ${e.message}"
                Log.e("NovaAportacioViewModel", "Error inesperat: ${e.message}")
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
                Log.d("NovaAportacioViewModel", "Carregant models per al manual $manualId...")
                when (val result = modelService.getModelsForManual(manualId)) {
                    is Result.Success -> {
                        _models.value = result.data
                        Log.d("NovaAportacioViewModel", "Models carregats: ${result.data.size}")
                    }
                    is Result.Error -> {
                        _notificacio.value = "Error carregant models: ${result.message}"
                        Log.e("NovaAportacioViewModel", "Error carregant models: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                _notificacio.value = "Error inesperat: ${e.message}"
                Log.e("NovaAportacioViewModel", "Error inesperat: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Funció no suspendida que llança una coroutine
    fun guardarAportacio(
        context: Context,
        manual: String?,
        model: String?,
        title: String,
        descripcio: String,
        imatges: List<Uri>,
        pdfUris: List<Uri>,
        videoUris: List<Uri>
    ) {
        scope.launch {
            guardarAportacioInternal(
                context = context,
                manual = manual,
                model = model,
                title = title,
                descripcio = descripcio,
                imatges = imatges,
                pdfUris = pdfUris,
                videoUris = videoUris
            )
        }
    }
    override fun onCleared() {
        super.onCleared()
        scope.cancel() // Cancel·la totes les coroutines quan el ViewModel es destrueix
    }
    // Guarda una nova aportació
    private suspend fun guardarAportacioInternal(
        context: Context,
        manual: String?,
        model: String?,
        title: String,
        descripcio: String,
        imatges: List<Uri>,
        pdfUris: List<Uri>,
        videoUris: List<Uri>
    ) {
        _isLoading.value = true
        _notificacio.value = ""

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _notificacio.value = "No s'ha trobat l'ID de l'usuari"
            _isLoading.value = false
            Log.e("NovaAportacioViewModel", "No s'ha trobat l'ID de l'usuari")
            return
        }

        // Obtenir les dades de l'usuari
        val user = when (val result = userService.getUser()) {
            is Result.Success -> {
                Log.d("NovaAportacioViewModel", "Dades de l'usuari obtingudes: ${result.data}")
                result.data
            }
            is Result.Error -> {
                _notificacio.value = "Error obtenint dades de l'usuari: ${result.message}"
                _isLoading.value = false
                Log.e("NovaAportacioViewModel", "Error obtenint dades de l'usuari: ${result.message}")
                return
            }
        }

        val userName = user?.name ?: "Usuari desconegut"

        // Generar l'ID de l'aportació
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val dateTime = dateFormat.format(Date())
        val sanitizedTitle = sanitizeTitle(title)
        val aportacioId = "${dateTime}_${sanitizedTitle}"
        Log.d("NovaAportacioViewModel", "ID de l'aportació generat: $aportacioId")

        // Obtenir la data i l'hora actuals
        val data = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        try {
            // Pujar imatges, PDFs i vídeos en paral·lel
            val (imageVideos, pdfVideos, videoVideos) = coroutineScope {
                val imagesDeferred = async {
                    aportacioService.pujarFitxersIGenerarMiniatures(
                        context, imatges, userId, aportacioId, "imatges"
                    )
                }
                val pdfsDeferred = async {
                    aportacioService.pujarFitxersIGenerarMiniatures(
                        context, pdfUris, userId, aportacioId, "pdfs"
                    )
                }
                val videosDeferred = async {
                    aportacioService.pujarFitxersIGenerarMiniatures(
                        context, videoUris, userId, aportacioId, "videos"
                    )
                }

                Triple(imagesDeferred.await(), pdfsDeferred.await(), videosDeferred.await())
            }

            // Combinar totes les URLs
            val totesLesUrls = imageVideos + pdfVideos + videoVideos
            Log.d("NovaAportacioViewModel", "Total d'URLs combinades: ${totesLesUrls.size}")

            // Crear l'objecte AportacioUser amb totes les dades
            val aportacio = AportacioUser(
                id = aportacioId,
                model = model ?: "",
                manual = manual ?: "",
                title = title,
                descripcio = descripcio,
                imageVideos = totesLesUrls,
                likes = 0,
                noLikes = 0,
                usageCount = 0,
                pdfUrls = pdfVideos.joinToString(",") { it.imageUrl },
                data = data,
                hora = hora,
                user = userId,
                userName = userName,
                usersWhoLiked = mutableListOf(),
                usersWhoDisliked = mutableListOf()
            )
            Log.d("NovaAportacioViewModel", "Aportació creada: $aportacio")

            // Guardar l'aportació a Firestore
            when (val result = aportacioService.addAportacio(aportacio)) {
                is Result.Success -> {
                    _notificacio.value = "Aportació guardada correctament"
                    Log.d("NovaAportacioViewModel", "Aportació guardada correctament")
                }
                is Result.Error -> {
                    _notificacio.value = "Error guardant l'aportació: ${result.message}"
                    Log.e("NovaAportacioViewModel", "Error guardant l'aportació: ${result.message}")
                }
            }
        } catch (e: Exception) {
            _notificacio.value = "Error inesperat: ${e.message}"
            Log.e("NovaAportacioViewModel", "Error inesperat: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }


}