package com.deixebledenkaito.autotechmanuals.data.service

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.deixebledenkaito.autotechmanuals.data.repository.PdfRepository
import com.google.firebase.storage.StorageReference
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result

class PdfService @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    // Obtenir els PDFs d'un model
    suspend fun obtenirPdfsDelModel(manualId: String, modelId: String): List<StorageReference> {
        return when (val result = pdfRepository.obtenirPdfsDelModel(manualId, modelId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("PdfService", "Error obtenint PDFs del model: ${result.message}")
                emptyList()
            }
        }
    }

    // Obtenir els PDFs d'una carpinteria
    suspend fun obtenirPdfsDeLaCarpinteria(
        manualId: String,
        modelId: String,
        carpinteriaId: String
    ): List<StorageReference> {
        return when (val result = pdfRepository.obtenirPdfsDeLaCarpinteria(manualId, modelId, carpinteriaId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("PdfService", "Error obtenint PDFs de la carpinteria: ${result.message}")
                emptyList()
            }
        }
    }

    // Descarregar un PDF
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun descarregarPdf(pdfRef: StorageReference, context: Context): Uri? {
        return when (val result = pdfRepository.descarregarPdf(pdfRef, context)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("PdfService", "Error descarregant PDF: ${result.message}")
                null
            }
        }
    }
}