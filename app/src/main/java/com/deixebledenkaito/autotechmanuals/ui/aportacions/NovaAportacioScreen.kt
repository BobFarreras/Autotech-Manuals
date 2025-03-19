package com.deixebledenkaito.autotechmanuals.ui.aportacions




import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource



import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.ui.Profile.SeleccionarImatgeDialog
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.loadingDialog.UploadProgressCard
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.videoPlayer.VideoPlayer

import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaAportacioScreen(
    viewModel: NovaAportacioViewModel = hiltViewModel(),
    navController: NavController
) {
    // Estats de la UI
    val manuals by viewModel.manuals.collectAsState()
    val models by viewModel.models.collectAsState()
    val selectedManual = remember { mutableStateOf("") }
    val selectedModel = remember { mutableStateOf("") }
    val expandedManual = remember { mutableStateOf(false) }
    val expandedModel = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val descripcio = remember { mutableStateOf("") }
    val imatges = remember { mutableStateListOf<Uri>() }
    val pdfUris = remember { mutableStateListOf<Uri>() }
    val videoUris = remember { mutableStateListOf<Uri>() }

    // Progrés de pujada
    val uploadProgress by viewModel.uploadProgress.collectAsState()

    // URI temporal per guardar la foto capturada amb la càmera
    val fotoCapturadaUri = remember { mutableStateOf<Uri?>(null) }

    // Crear un fitxer temporal per guardar la foto capturada amb la càmera
    val context = LocalContext.current
    val fotoTemporal = remember {
        File.createTempFile(
            "temp_image",
            ".jpg",
            context.externalCacheDir
        ).apply {
            deleteOnExit()
        }
    }
    // URI del fitxer temporal
    val fotoTemporalUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            fotoTemporal
        )
    }

    // Estat de càrrega
    val isLoading by viewModel.isLoading.collectAsState()

    // Snackbar per mostrar notificacions
    val snackbarHostState = remember { SnackbarHostState() }

    // Escoltar canvis en la notificació del ViewModel
    val notificacio by viewModel.notificacio.collectAsState()

    LaunchedEffect(notificacio) {
        if (notificacio.isNotEmpty()) {
            snackbarHostState.showSnackbar(notificacio)
            navController.navigate("profile") { // Tornar a la pantalla de Profile
                popUpTo("novaAportacio") { inclusive = true } // Eliminar la pantalla actual de l'stack
            }
        }
    }

    // Launcher per seleccionar imatges
    val launcherImatges = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imatges.add(it)
            }
        }
    )

    // Launcher per obrir la càmera
    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                fotoCapturadaUri.value?.let { uri ->
                    imatges.add(uri)
                }
            }
        }
    )

    // Estat per controlar si es mostra el diàleg
    val mostrarDialog = remember { mutableStateOf(false) }

    // Diàleg per triar entre galeria i càmera
    if (mostrarDialog.value) {
        SeleccionarImatgeDialog(
            onGaleriaClick = {
                launcherImatges.launch("image/*")
                mostrarDialog.value = false
            },
            onCameraClick = {
                fotoCapturadaUri.value = fotoTemporalUri
                launcherCamera.launch(fotoTemporalUri)
                mostrarDialog.value = false
            },
            onDismiss = { mostrarDialog.value = false }
        )
    }

    // Launcher per seleccionar múltiples PDFs
    val launcherPDFs = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            uris.let {
                pdfUris.addAll(it)
            }
        }
    )

    // Launcher per seleccionar múltiples vídeos
    val launcherVideos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            uris.let {
                videoUris.addAll(it)
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Cridar la funció no suspendida del ViewModel
                    viewModel.guardarAportacio(
                        context = context,
                        manual = selectedManual.value,
                        model = selectedModel.value,
                        title = title.value,
                        descripcio = descripcio.value,
                        imatges = imatges,
                        pdfUris = pdfUris,
                        videoUris = videoUris
                    )
                },
                modifier = Modifier.padding(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Check, contentDescription = "Guardar")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    // Selecció de manual
                    Text("Selecciona un manual", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedManual.value,
                        onExpandedChange = { expandedManual.value = it }
                    ) {
                        TextField(
                            value = selectedManual.value,
                            onValueChange = { selectedManual.value = it },
                            label = { Text("Manual") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            enabled = !isLoading
                        )
                        ExposedDropdownMenu(
                            expanded = expandedManual.value,
                            onDismissRequest = { expandedManual.value = false }
                        ) {
                            manuals.forEach { manual ->
                                DropdownMenuItem(
                                    text = { Text(manual.nom) },
                                    onClick = {
                                        selectedManual.value = manual.nom
                                        viewModel.carregarModels(manual.id)
                                        expandedManual.value = false
                                    },
                                    enabled = !isLoading
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Selecció de model
                    Text("Selecciona un model", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedModel.value,
                        onExpandedChange = { expandedModel.value = it }
                    ) {
                        TextField(
                            value = selectedModel.value,
                            onValueChange = { selectedModel.value = it },
                            label = { Text("Model") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            enabled = !isLoading
                        )
                        ExposedDropdownMenu(
                            expanded = expandedModel.value,
                            onDismissRequest = { expandedModel.value = false }
                        ) {
                            models.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model.nom) },
                                    onClick = {
                                        selectedModel.value = model.nom
                                        expandedModel.value = false
                                    },
                                    enabled = !isLoading
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Títol de l'aportació
                    Text("Títol", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Títol de l'aportació") },
                        maxLines = 3,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Descripció de l'aportació
                    Text("Descripció", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = descripcio.value,
                        onValueChange = { descripcio.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripció de l'aportació") },
                        maxLines = 20,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Pujar imatges
                    Text("Puja imatges", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            mostrarDialog.value = true  // Mostrar el diàleg
                        },
                        enabled = !isLoading
                    ) {
                        Text("Selecciona imatges")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Previsualització de les imatges
                    imatges.forEachIndexed { index, uri ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Image(
                                    painter = rememberImagePainter(uri),
                                    contentDescription = "Imatge de l'aportació",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { imatges.removeAt(index) },
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    enabled = !isLoading
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar imatge")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Pujar vídeos
                    Text("Puja vídeos", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { launcherVideos.launch(arrayOf("video/*")) },
                        enabled = !isLoading,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Selecciona vídeos")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Previsualització dels vídeos
                    videoUris.forEachIndexed { index, uri ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Reproductor de vídeo
                                VideoPlayer(
                                    videoUrl = uri.toString(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )

                                // Botó per eliminar el vídeo
                                IconButton(
                                    onClick = { videoUris.removeAt(index) },
                                    modifier = Modifier.align(Alignment.End),
                                    enabled = !isLoading
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar vídeo")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Pujar PDFs
                    Text("Puja PDFs", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { launcherPDFs.launch(arrayOf("application/pdf")) },
                        enabled = !isLoading
                    ) {
                        Text("Selecciona PDFs")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Previsualització dels PDFs
                    pdfUris.forEachIndexed { index, uri ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logopdf),
                                    contentDescription = "PDF Icon",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uri.lastPathSegment ?: "PDF",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { pdfUris.removeAt(index) },
                                    enabled = !isLoading
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar PDF")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }

            // Mostrar l'indicador de càrrega
            if (isLoading) {
                UploadProgressCard(
                    uploadProgress = uploadProgress,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            // Mostrar el progrés de pujada
            if (uploadProgress > 0f) {
                LinearProgressIndicator(
                    progress = uploadProgress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}