package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnCarpinteria

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.loadingDialog.MessageDialog
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.saveRouteButton.SaveRouteButton
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.sharedViewModel.SharedViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ESGScreen(
    navController: NavController,
    manualId: String,
    modelId: String,
    carpinteriaID :String,
    viewModel: ESGViewModel = hiltViewModel() ,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    // Context per obrir el PDF
    val context = LocalContext.current
    // Observar l'estat del ViewModel
    val pdfs by viewModel.pdfs.collectAsState()
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
    val showMessageDialog by sharedViewModel.showMessageDialog.collectAsState()
    val messageDialogText by sharedViewModel.messageDialogText.collectAsState()


    // Carregar els PDFs del model
    LaunchedEffect(manualId, modelId) {
        // Demanar permisos
        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        viewModel.carregarPdfsESG(manualId, modelId, carpinteriaID)
    }

    // Mostra el diàleg de missatges
    MessageDialog(
        showDialog = showMessageDialog,
        message = messageDialogText,
        onDismiss = { sharedViewModel.hideMessageDialog() }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ESG") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back), // Substitueix amb el teu drawable
                            contentDescription = "Tornar",
                            modifier = Modifier.size(24.dp) // Ajusta la mida segons sigui necessari
                        )
                    }
                },
                actions = {
                    SaveRouteButton(
                        sharedViewModel = sharedViewModel,
                        onSaveRoute = {
                            RutaGuardada(
                                id = UUID.randomUUID().toString(),
                                nom = "$manualId -> $modelId -> Carpinteria ESG",
                                ruta = "esg/$manualId/$modelId",
                                dataGuardat = System.currentTimeMillis()
                            )
                        },
                        modifier = Modifier.padding(16.dp).width(34.dp)
                    )
                }
            )
        },

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Nom del PDF
            Text(
                text = "Manuals Carpinteria ESG",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Llista de PDFs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
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
                                .width(150.dp)
                                .height(170.dp)
                                .padding(8.dp)
                        ) {
                            // Imatge del PDF
                            Image(
                                painter = painterResource(id = R.drawable.logopdf),
                                contentDescription = "Imatge del PDF",
                                modifier = Modifier
                                    .size(38.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Nom del PDF
                            Text(
                                text = pdfRef.name,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Botó per descarregar el PDF
                            Button(
                                onClick = {
                                    viewModel.descarregarPdf(pdfRef, context)
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                    .width(120.dp)

                            ) {
                                Text(
                                    text = "Descarregar",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espai entre les dues cards

            // Card per a "AutoDiespiece"
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Ocupa el 80% de l'amplada de la pantalla
                    .padding(8.dp)
                    .clickable { navController.navigate("autoDespieceHome") },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally, // Centra el contingut horitzontalment
                    verticalArrangement = Arrangement.Center // Centra el contingut verticalment
                ) {
                    // Imatge a dalt
                    Image(
                        painter = painterResource(id = R.drawable.logo_v1), // Canvia `auto_diespiece_image` per la teva imatge
                        contentDescription = "AutoDiespiece Image",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Espai entre la imatge i el títol
                    // Títol a sota
                    Text(
                        text = "AutoEspecejament",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center // Centra el text
                    )
                }
            }
        }
    }
}