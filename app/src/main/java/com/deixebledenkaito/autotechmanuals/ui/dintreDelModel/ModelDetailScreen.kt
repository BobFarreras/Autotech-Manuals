package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.loadingDialog.MessageDialog
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.saveRouteButton.SaveRouteButton
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.sharedViewModel.SharedViewModel
import java.util.UUID
//AIXO ES DINTRE EL MODEL AMB ELS BOTONS I LES APORTACIONS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDetailScreen(
    manualId: String,
    modelId: String,
    navController: NavController,
    viewModel: ModelDetailViewModel = hiltViewModel() ,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {

    // Observar l'estat del ViewModel
    val model by viewModel.model.collectAsState()
    val aportacions by viewModel.aportacions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val showMessageDialog by sharedViewModel.showMessageDialog.collectAsState()
    val messageDialogText by sharedViewModel.messageDialogText.collectAsState()

    // Carregar els PDFs del model
    LaunchedEffect(manualId, modelId) {
        // Carregar les dades del model i les aportacions
        viewModel.loadModelAndAportacions(manualId, modelId)
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
                title = { Text(model?.nom ?: "Detalls del model") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tornar"
                        )
                    }
                },
                actions = {
                    SaveRouteButton(
                        sharedViewModel = sharedViewModel,
                        onSaveRoute = {
                            RutaGuardada(
                                id = UUID.randomUUID().toString(),
                                nom = "$manualId -> $modelId",
                                ruta = "modelDetail/${manualId}/${modelId}",
                                dataGuardat = System.currentTimeMillis()
                            )
                        },
                        modifier = Modifier.padding(16.dp).width(34.dp)
                    )
                }
            )
        },

    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }
        } else if (model != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                // Secció superior: Imatge i descripció del model
                ModelHeader(model = model!!)



                // 6 targetes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Errors", "Manuals", "Connexions", "Instal·lació","Paràmetres", "Carpinteria")) { cardName ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clickable {
                                    // Aquí pots afegir la lògica per a cada targeta
                                    when (cardName) {
                                        "Errors" -> {  navController.navigate("errorsDelModel/$manualId/$modelId") }
                                        "Manuals" -> { navController.navigate("descarregarManuals/$manualId/$modelId")}
                                        "Connexions" -> { navController.navigate("Coneccions") }
                                        "Instal·lació" -> { /* Navegar a la pantalla d'instal·lació */ }
                                        "Paràmetres" -> { navController.navigate("parametres") }
                                        "Carpinteria" -> { navController.navigate("carpinteria/$manualId/$modelId")}
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = cardName,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }



                // Llista d'aportacions
                Text(
                    text = "Aportacions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )

                if (aportacions.isEmpty()) {
                    Text(
                        text = "No hi ha aportacions disponibles.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        items(aportacions) { aportacio ->


                            AportacioCardDetail(
                                aportacio = aportacio ,
                                onClick = {
                                    // Navegar a la nova pantalla amb l'ID de l'aportació
                                    navController.navigate("aportacioDetailHome/${aportacio.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ModelHeader(model: Model?) {
    if (model == null) return // Si no hi ha manual, no es mostra res

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imatge del manual
            Image(
                painter = rememberImagePainter(model.imageUrl),
                contentDescription = "Manual Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripció de l'aportació (amb limitació de línies)
            var showFullDescription by remember { mutableStateOf(false) }

            Text(
                text = model.descripcio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { showFullDescription = !showFullDescription }.padding(12.dp)

            )

            // Diàleg per mostrar la descripció completa
            if (showFullDescription) {
                AlertDialog(
                    onDismissRequest = { showFullDescription = false },
                    title = { Text("Descripció completa") },
                    text = {
                        Text(
                            text =  model.descripcio,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showFullDescription = false }) {
                            Text("Tancar")
                        }
                    }
                )
            }
        }
    }
}

