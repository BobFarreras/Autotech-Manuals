package com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen



import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.lifecycle.viewModelScope

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
import com.deixebledenkaito.autotechmanuals.ui.aportacions.AportacioCard
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDetailScreen(
    manualId: String,
    modelId: String,
    navController: NavController,
    viewModel: ModelDetailViewModel = hiltViewModel()
) {
    // Carregar les dades del model i les aportacions
    LaunchedEffect(manualId, modelId) {
        viewModel.loadModelAndAportacions(manualId, modelId)
    }

    // Observar l'estat del ViewModel
    val model by viewModel.model.collectAsState()
    val aportacions by viewModel.aportacions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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
                }
            )
        }
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

                Spacer(modifier = Modifier.height(16.dp))

                // Quatre targetes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Errors", "Manuals", "Connexions", "Instal·lació")) { cardName ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable {
                                    // Aquí pots afegir la lògica per a cada targeta
                                    when (cardName) {
                                        "Errors" -> { /* Navegar a la pantalla d'errors */ }
                                        "Manuals" -> { /* Navegar a la pantalla de manuals */ }
                                        "Connexions" -> { /* Navegar a la pantalla de connexions */ }
                                        "Instal·lació" -> { /* Navegar a la pantalla d'instal·lació */ }
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Llista d'aportacions
                Text(
                    text = "Aportacions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
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
                            .padding(horizontal = 16.dp)
                    ) {
                        items(aportacions) { aportacio ->
                            var showDeleteConfirmation by remember { mutableStateOf(false) }

                            if (showDeleteConfirmation) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteConfirmation = false },
                                    title = { Text("Eliminar aportació") },
                                    text = { Text("Estàs segur que vols eliminar aquesta aportació?") },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showDeleteConfirmation = false
                                                coroutineScope.launch {
                                                    val eliminada = viewModel.eliminarAportacio(aportacio)
                                                    if (eliminada) {
                                                        // Actualitza la llista d'aportacions després de l'eliminació
                                                        viewModel.loadModelAndAportacions(manualId, modelId)
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("Eliminar")
                                        }
                                    },
                                    dismissButton = {
                                        Button(
                                            onClick = { showDeleteConfirmation = false }
                                        ) {
                                            Text("Cancel·lar")
                                        }
                                    }
                                )
                            }

                            AportacioCard(
                                aportacio = aportacio,
                                onDelete = {
                                    showDeleteConfirmation = true
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
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(8.dp))

            // Descripció de l'aportació (amb limitació de línies)
            var showFullDescription by remember { mutableStateOf(false) }

            Text(
                text = model.descripcio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { showFullDescription = !showFullDescription }
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
