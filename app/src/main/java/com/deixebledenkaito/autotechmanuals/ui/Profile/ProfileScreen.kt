

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

import com.deixebledenkaito.autotechmanuals.ui.Profile.ProfileViewModel
import com.deixebledenkaito.autotechmanuals.ui.aportacions.CardAportacions.AportacioPropiaCard


import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.loadingDialog.LoadingDialog
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.BackgroundColor
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.title
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userAportacions by viewModel.userAportacions.collectAsState()
    // Estat per controlar si es mostra el diàleg de selecció d'imatge
    var showImagePickerDialog by remember { mutableStateOf(false) }

    // Launcher per obrir la galeria
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            viewModel.updateProfileImage(selectedImageUri)
        }
    }

    // Carrega les dades de l'usuari quan la pantalla es mostra
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModel.loadUser()
        viewModel.loadUserAportacions(userId)
    }

    // Mostra el Dialog de càrrega si isLoading és true
    if (isLoading) {
        LoadingDialog(isLoading = isLoading, message = "Guardant imatge de perfil...")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Imatge de perfil (si existeix)
                        user?.profileImageUrl?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl, // URL de la imatge
                                contentDescription = "Imatge de perfil",
                                contentScale = ContentScale.FillBounds, // Evita que la imatge es talli
                                modifier = Modifier
                                    .size(70.dp) // Mida de la imatge
                                    .clip(CircleShape) // Forma rodona
                                    .clickable {
                                        showImagePickerDialog = true
                                    } // Obrir diàleg en fer clic
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Espai entre la imatge i el text
                        }
                        Column {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user?.name ?: "Usuari",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrelles",
                                    tint = Color.Yellow
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = user?.stars?.toString() ?: "0",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user?.description ?: "Descripció de l'usuari",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor, // Fons de la TopAppBar
                    titleContentColor = title // Color del text
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("novaAportacio")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Afegir aportació")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .wrapContentHeight() // S'ajusta a l'alçada del contingut
                .padding(16.dp)
        ) {
            items(userAportacions) { aportacio ->
                AportacioPropiaCard(
                    aportacio = aportacio,
                    onDelete = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        viewModel.eliminarAportacio(userId, aportacio)
                    },
                    onClick = {
                        navController.navigate("aportacioDetail/${aportacio.id}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Canviar imatge de perfil") },
            text = {
                Column {
                    Text("Selecciona una nova imatge de la galeria.")
                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pujant imatge...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImagePickerDialog = false
                        launcher.launch("image/*")
                    },
                    enabled = !isLoading // Desactiva el botó mentre es puja la imatge
                ) {
                    Text("Seleccionar imatge")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showImagePickerDialog = false },
                    enabled = !isLoading // Desactiva el botó mentre es puja la imatge
                ) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}