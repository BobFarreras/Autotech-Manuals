package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnCarpinteria

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.FirebaseDataBaseService
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ESGViewModel @Inject constructor(
    private val firebaseDataBaseService: FirebaseDataBaseService
) : ViewModel() {

    private val _pdfs = MutableStateFlow<List<StorageReference>>(emptyList())
    val pdfs: StateFlow<List<StorageReference>> get() = _pdfs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    // Carregar la llista de PDFs
    fun carregarPdfsESG(manualId: String, modelId: String , carpinteriaId : String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val pdfs = firebaseDataBaseService.obtenirPdfsDeLaCarpinteria(manualId, modelId, carpinteriaId )
                _pdfs.value = pdfs
            } catch (e: Exception) {
                _errorMessage.value = "Error carregant PDFs: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Descàrrega d'un PDF
    @RequiresApi(Build.VERSION_CODES.Q)
    fun descarregarPdf(pdfRef: StorageReference, context: Context) {
        viewModelScope.launch {
            try {
                // Descarregar el PDF
                val uri = firebaseDataBaseService.descarregarPdf(pdfRef, context)

                // Comprovar si el fitxer s'ha descarregat correctament
                if (uri != null) {
                    Log.d("DescarregarMimdesCarpinteria", "PDF descarregat: $uri")

                    // Obrir el PDF amb un visualitzador extern
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY

                    // Comprovar si hi ha una aplicació que pugui gestionar la Intent
                    val packageManager = context.packageManager
                    if (intent.resolveActivity(packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        _errorMessage.value = "No s'ha trobat cap aplicació per obrir PDFs."
                    }
                } else {
                    _errorMessage.value = "Error descarregant el PDF."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error descarregant el PDF: ${e.message}"
                Log.e("DescarregarMimdesCarpinteria", "Error descarregant PDF", e)
            }
        }
    }
}