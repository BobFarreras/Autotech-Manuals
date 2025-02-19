package com.deixebledenkaito.autotechmanuals.ui.home

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

import com.deixebledenkaito.autotechmanuals.domain.Manuals
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val viewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel)

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    // Observa l'estat dels manuals, els manuals més populars i l'últim manual
    val manuals by viewModel.manuals.collectAsState()
    val topManuals by viewModel.topManuals.collectAsState()
    val lastManual by viewModel.lastManual.collectAsState()
    val user by viewModel.user.collectAsState()

    var showAddManualDialog by remember { mutableStateOf(false) }
    // Carrega les dades quan la pantalla es mostra per primera vegada
    LaunchedEffect(Unit) {
        viewModel.loadManuals() // Carrega tots els manuals
        viewModel.loadTopManuals() // Carrega els manuals més populars
        viewModel.loadLastManual() // Carrega l'últim manual afegit
        viewModel.loadUser() // Carrega les dades de l'usuari
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Benvingut, ${user?: "Usuari"}") },
                actions = {
                    IconButton(onClick = { viewModel.addManual(
                        nom = TODO(),
                        descripcio = TODO(),
                        imageUri = TODO()
                    ) }) {
                        Icon(Icons.Default.Add, contentDescription = "Afegir manual")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Tancar sessió")
                    }
                }
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Últim manual utilitzat
            lastManual?.let { manual ->
                Text("Últim manual utilitzat", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ManualItem(manual = manual)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Llista de manuals més populars (horitzontal)
            Text("Manuals més populars", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(topManuals) { manual ->
                    ManualItem(manual = manual, modifier = Modifier.width(200.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Llista de tots els manuals (vertical)
            Text("Tots els manuals", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(manuals) { manual ->
                    ManualItem(manual = manual)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    if (showAddManualDialog) {
        AddManualDialog(
            onDismiss = { showAddManualDialog = false },
            onConfirm = { nom, descripcio, imageUri ->
                viewModel.addManual(nom, descripcio, imageUri)
                showAddManualDialog = false
            }
        )
    }
}
@Composable
fun ManualItem(manual: Manuals, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Imatge del manual
            Image(
                painter = rememberImagePainter(manual.imageUrl),
                contentDescription = manual.nom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Nom del manual
            Text(
                text = manual.nom,
                style = MaterialTheme.typography.titleLarge // Estil per al títol
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = manual.descripcio,
                style = MaterialTheme.typography.bodyMedium // Estil per al cos del text
            )
        }
    }
}

@Composable
fun AddManualDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Uri) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var descripcio by remember { mutableStateOf("") }
    val imageUri by remember { mutableStateOf<Uri?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Afegir manual") },
        text = {
            Column {
                TextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom del manual") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = descripcio,
                    onValueChange = { descripcio = it },
                    label = { Text("Descripció del manual") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Obrir selector d'imatges */ }) {
                    Text("Seleccionar imatge")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                imageUri?.let { uri ->
                    onConfirm(nom, descripcio, uri)
                }
            }) {
                Text("Afegir")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel·lar")
            }
        }
    )
}

