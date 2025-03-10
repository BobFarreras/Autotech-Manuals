package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.lleguirPdf

import android.graphics.Bitmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PdfViewerScreen(pdfFilePath: String) {
    val pdfReader = PdfReader(LocalContext.current)
    val pdfFile = File(pdfFilePath)
// Pàgines que vols mostrar (per exemple, pàgines 1, 2 i 3)
    val pagesToLoad = listOf(10, 11, 20, 23,27,30,35,36,38,46)

    // Carrega les pàgines del PDF
    val pdfPages by remember { mutableStateOf(pdfReader.loadPdfPages(pdfFile, pagesToLoad)) }

    // Mostra el contingut del PDF
    PdfContentViewer(pdfPages = pdfPages)
}

@Composable
fun PdfContentViewer(pdfPages: List<Bitmap>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Mostra cada pàgina del PDF
        pdfPages.forEach { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Pàgina del PDF",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}