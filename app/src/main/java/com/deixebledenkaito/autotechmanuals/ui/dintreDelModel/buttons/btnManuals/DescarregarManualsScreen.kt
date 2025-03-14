package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnManuals

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deixebledenkaito.autotechmanuals.R

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DescarregarManualsScreen(
    manualId: String,
    modelId: String,
    navController: NavController,
    viewModel: DescarregarManualsViewModel = hiltViewModel()
) {
    // Observar l'estat del ViewModel
    val pdfs by viewModel.pdfs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Context per obrir el PDF
    val context = LocalContext.current

    // Demanar permisos d'emmagatzematge
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permissions", "Permís concedit")
        } else {
            Log.d("Permissions", "Permís denegat")
        }
    }

    // Carregar els PDFs del model
    LaunchedEffect(manualId, modelId) {
        // Demanar permisos
        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        viewModel.carregarPdfs(manualId, modelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Descarregar Manuals") },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Tornar",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.Black) // Canvia el color de l'icona
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Mostrar l'estat de càrrega
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Mostrar missatges d'error
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Comprovar si hi ha PDFs disponibles
            if (pdfs.isEmpty()) {
                Text(
                    text = "No hi ha PDFs disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Llista de PDFs
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Funció items per a cada PDF
                    items(pdfs) { pdfRef ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Imatge del PDF
                                Image(
                                    painter = painterResource(id = R.drawable.logopdf),
                                    contentDescription = "Imatge del PDF",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Nom del PDF
                                Text(
                                    text = pdfRef.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Botó per descarregar el PDF
                                Button(
                                    onClick = {
                                        viewModel.descarregarPdf(pdfRef, context)
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Descarregar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}