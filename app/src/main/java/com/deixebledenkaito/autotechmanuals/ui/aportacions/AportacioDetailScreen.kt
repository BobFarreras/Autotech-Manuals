package com.deixebledenkaito.autotechmanuals.ui.aportacions


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.videoPlayer.VideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AportacioDetailScreen(
    aportacioId: String,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    navController: NavController
) {
    val aportacio = AportacioData.aportacions.find { it.id == aportacioId }
    Log.d("AportacioDetailScreen", "Aportació ${AportacioData.aportacions} aportacioId ${aportacioId}")

    if (aportacio == null) {
        Log.d("AportacioDetailScreen", "Aportació no trobada. Mostrant indicador de càrrega...")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Aportació no trobada")
        }
    } else {
        Log.d("AportacioDetailScreen", "Aportació trobada. Mostrant dades...")
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = aportacio.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.width(40.dp))
                            // Botó de dislike
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                IconButton(
                                    onClick = onDislike,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_nolike),
                                        contentDescription = "No Like",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Text(
                                    text = aportacio.noLikes.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // Botó de like
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onLike,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_like),
                                        contentDescription = "Like",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Text(
                                    text = aportacio.likes.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Tornar")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Secció superior: Descripció
                Text(
                    text = aportacio.descripcio,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )

                // Imatges
                if (aportacio.imageVideos.any { true }) {
                    Text(
                        text = "Imatges",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(aportacio.imageVideos.filter { true }) { imageVideo ->
                            var showFullScreenImage by remember { mutableStateOf(false) }
                            AsyncImage(
                                model = imageVideo.imageUrl,
                                contentDescription = "Imatge",
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showFullScreenImage = true },
                                contentScale = ContentScale.Crop
                            )
                            if (showFullScreenImage) {
                                Dialog(
                                    onDismissRequest = { showFullScreenImage = false },
                                    properties = DialogProperties(usePlatformDefaultWidth = false)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black)
                                    ) {
                                        AsyncImage(
                                            model = imageVideo.imageUrl,
                                            contentDescription = "Imatge a pantalla completa",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Vídeos
                if (aportacio.imageVideos.any { it.videoUrl != null }) {
                    Text(
                        text = "Vídeos",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(aportacio.imageVideos.filter { it.videoUrl != null }) { imageVideo ->
                            var showFullScreenVideo by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showFullScreenVideo = true }
                            ) {
                                VideoPlayer(
                                    videoUrl = imageVideo.videoUrl.toString(),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (showFullScreenVideo) {
                                Dialog(
                                    onDismissRequest = { showFullScreenVideo = false },
                                    properties = DialogProperties(usePlatformDefaultWidth = false)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black)
                                    ) {
                                        VideoPlayer(
                                            videoUrl = imageVideo.videoUrl.toString(),
                                            modifier = Modifier.fillMaxSize(),
                                            isFullScreen = true, // Afegir aquesta propietat al teu VideoPlayer
                                            autoPlay = true // Reprodueix automàticament
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // PDFs
                if (!aportacio.pdfUrls.isNullOrEmpty()) {
                    Text(
                        text = "Documents PDF",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        aportacio.pdfUrls.split(",").forEach { pdfUrl ->
                            val trimmedUrl = pdfUrl.trim()
                            if (trimmedUrl.isNotEmpty()) {
                                PdfDownloadCard(
                                    pdfUrl = trimmedUrl,
                                    onDownload = {
                                        downloadPdf(trimmedUrl)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfDownloadCard(pdfUrl: String, onDownload: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Descarregar PDF",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onDownload) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Descarregar PDF"
                )
            }
        }
    }
}

fun downloadPdf(pdfUrl: String) {
    // Implementa la lògica per descarregar el PDF aquí
    Log.d("AportacioDetailScreen", "Descarregant PDF: $pdfUrl")
}