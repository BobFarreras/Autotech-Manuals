package com.deixebledenkaito.autotechmanuals.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// data/repository/PdfRepository.kt
class PdfRepository @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun obtenirPdfsDelModel(manualId: String, modelId: String): Result<List<StorageReference>> {
        return try {
            val pdfsRef = storage.reference.child("manuals/$manualId/$modelId/pdfManuals")
            val listResult = pdfsRef.listAll().await()
            Log.d("PdfRepository", "PDFs trobats: ${listResult.items.size}")
            Result.Success(listResult.items)
        } catch (e: Exception) {
            Log.e("PdfRepository", "Error obtenint PDFs: ${e.message}")
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun obtenirPdfsDeLaCarpinteria(
        manualId: String,
        modelId: String,
        carpinteriaId: String
    ): Result<List<StorageReference>> {
        return try {
            val pdfsRef = storage.reference.child("manuals/$manualId/$modelId/pdfCarpinteria/$carpinteriaId")
            val listResult = pdfsRef.listAll().await()
            Log.d("PdfRepository", "PDFs trobats: ${listResult.items.size}")
            Result.Success(listResult.items)
        } catch (e: Exception) {
            Log.e("PdfRepository", "Error obtenint PDFs: ${e.message}")
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun descarregarPdf(pdfRef: StorageReference, context: Context): Result<Uri?> {
        return withContext(Dispatchers.IO) {
            try {
                // Obtenir el nom del fitxer
                val fileName = pdfRef.name

                // Crear un fitxer a la carpeta de descàrregues utilitzant MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                // Inserir el fitxer a la carpeta de descàrregues
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    // Descarregar el PDF directament a un fitxer temporal
                    val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
                    pdfRef.getFile(tempFile).await()

                    // Copiar el contingut del fitxer temporal a la ubicació final
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        tempFile.inputStream().copyTo(outputStream)
                    }

                    // Eliminar el fitxer temporal
                    tempFile.delete()

                    Log.d("PdfRepository", "PDF descarregat correctament: $uri")
                    Result.Success(uri)
                } else {
                    Log.e("PdfRepository", "No s'ha pogut crear el fitxer a la carpeta de descàrregues.")
                    Result.Success(null)
                }
            } catch (e: Exception) {
                Log.e("PdfRepository", "Error descarregant PDF: ${e.message}", e)
                Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
            }
        }
    }
}