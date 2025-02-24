package com.deixebledenkaito.autotechmanuals.ui.aportacions

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
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


@HiltViewModel
class NovaAportacioViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService,
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

    private fun carregarManuals() {
        viewModelScope.launch {
            _manuals.value = firebaseDataBaseService.totsElsManuals()
        }
    }

    fun carregarModels(manualId: String) {
        viewModelScope.launch {
            _models.value = firebaseDataBaseService.getModelsForManual(manualId)
        }
    }

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
            _isLoading.value = true // Iniciar càrrega
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                mostrarNotificacio("No s'ha trobat l'ID de l'usuari")
                _isLoading.value = false // Finalitzar càrrega
                return@launch
            }
            // Obtenir les dades de l'usuari des de Firestore
            val user = firebaseDataBaseService.getUser()
            val userName = user?.name ?: "Usuari desconegut"


            // Generar l'ID com a data/hora/min/segons + el nom del títol
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val dateTime = dateFormat.format(Date())
            val aportacioId = "${dateTime}_${title.replace(" ", "_")}"

            // Obtenir la data i l'hora actuals
            val data = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            Log.d("NovaAportacioViewModel", "Iniciant guardat de l'aportació: $aportacioId")

            try {
                // Pujar imatges (si n'hi ha)
                val imatgeUrls = if (imatges.isNotEmpty()) {
                    mutableListOf<String>().apply {
                        for (uri in imatges) {
                            val imatgeName = UUID.randomUUID().toString()
                            val imatgeRef = storage.reference.child("usuaris/$userId/aportacions/$aportacioId/imatges/$imatgeName")
                            Log.d("NovaAportacioViewModel", "Pujant imatge: $imatgeName")
                            val uploadTask = imatgeRef.putFile(uri).await()
                            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                            add(downloadUrl)
                            Log.d("NovaAportacioViewModel", "Imatge pujada: $downloadUrl")
                        }
                    }
                } else {
                    emptyList() // Si no hi ha imatges, retorna una llista buida
                }

                // Pujar PDFs (si n'hi ha)
                val pdfUrls = if (pdfUris.isNotEmpty()) {
                    mutableListOf<String>().apply {
                        for (uri in pdfUris) {
                            val pdfName = UUID.randomUUID().toString()
                            val pdfRef = storage.reference.child("usuaris/$userId/aportacions/$aportacioId/pdfs/$pdfName")
                            Log.d("NovaAportacioViewModel", "Pujant PDF: $pdfName")
                            val uploadTask = pdfRef.putFile(uri).await()
                            val downloadUrlPdf = uploadTask.storage.downloadUrl.await().toString()
                            add(downloadUrlPdf)
                            Log.d("NovaAportacioViewModel", "PDF pujat: $downloadUrlPdf")
                        }
                    }
                } else {
                    emptyList() // Si no hi ha PDFs, retorna una llista buida
                }

                // Pujar vídeos (si n'hi ha)
                val videoUrls = if (videoUris.isNotEmpty()) {
                    mutableListOf<String>().apply {
                        for (uri in videoUris) {
                            val videoName = UUID.randomUUID().toString()
                            val videoRef = storage.reference.child("usuaris/$userId/aportacions/$aportacioId/videos/$videoName")
                            Log.d("NovaAportacioViewModel", "Pujant vídeo: $videoName")
                            val uploadTask = videoRef.putFile(uri).await()
                            val downloadUrlVideo = uploadTask.storage.downloadUrl.await().toString()
                            add(downloadUrlVideo)
                            Log.d("NovaAportacioViewModel", "Vídeo pujat: $downloadUrlVideo")
                        }
                    }
                } else {
                    emptyList() // Si no hi ha vídeos, retorna una llista buida
                }

                // Guardar l'aportació a Firestore
                val aportacio = AportacioUser(
                    id = aportacioId,
                    model = model ?: "",
                    manual = manual ?: "",
                    title = title,
                    descripcio = descripcio,
                    imageUrls = imatgeUrls.joinToString(","), // Converteix la llista a una cadena separada per comes
                    stars = 0,
                    usageCount = 0,
                    pdfUrls = pdfUrls.joinToString(","), // Converteix la llista a una cadena separada per comes
                    videoUrls = videoUrls.joinToString(","), // Converteix la llista a una cadena separada per comes
                    data = data,
                    hora = hora,
                    user = userId,
                    userName = userName // Afegim el nom de l'usuari
                )
                Log.d("NovaAportacioViewModel", "Guardant aportació a Firestore: $aportacio")
                val success = firebaseDataBaseService.addAportacio(userId, aportacio)
                val aportacioAlManual = firebaseDataBaseService.addAportacioEnElManual(userId, aportacio)
                if (success) {
                    Log.d("NovaAportacioViewModel", "Aportació guardada correctament")
                    mostrarNotificacio("Aportació guardada correctament")
                } else {
                    Log.e("NovaAportacioViewModel", "Error guardant l'aportació")
                    mostrarNotificacio("Error guardant l'aportació")
                }
                if (aportacioAlManual) {
                    Log.d("NovaAportacioViewModel", "Aportació guardada correctament")
                    mostrarNotificacio("Aportació guardada correctament")
                } else {
                    Log.e("NovaAportacioViewModel", "Error guardant l'aportació")
                    mostrarNotificacio("Error guardant l'aportació")
                }
            } catch (e: Exception) {
                Log.e("NovaAportacioViewModel", "Error: ${e.message}")
                mostrarNotificacio("Error: ${e.message}")
            } finally {
                _isLoading.value = false // Finalitzar càrrega
            }
        }
    }

    private fun mostrarNotificacio(missatge: String) {
        viewModelScope.launch {
            _notificacio.emit(missatge)



        }
    }
}