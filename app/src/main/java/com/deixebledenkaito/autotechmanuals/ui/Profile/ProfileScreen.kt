import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp

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
import coil.compose.rememberImagePainter



import com.deixebledenkaito.autotechmanuals.ui.Profile.ProfileViewModel
import com.deixebledenkaito.autotechmanuals.ui.aportacions.AportacioCard
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.BackgroundColor
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.title


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val user by viewModel.user.collectAsState()
    val userAportacions by viewModel.userAportacions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Imatge de perfil (si existeix)
                        user?.profileImageUrl?.let { imageUrl ->
                            Image(
                                painter = rememberImagePainter(imageUrl),
                                contentDescription = "Imatge de perfil",
                                contentScale = ContentScale.FillBounds, // Evita que la imatge es talli
                                modifier = Modifier
                                    .size(70.dp) // Mida de la imatge
                                    .clip(CircleShape) // Forma rodona

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
                ),
                actions = {
                    IconButton(
                        onClick = {
                            println("A fet click")
                        }
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Tancar sessió")
                    }
                }
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
                .fillMaxSize()
                .padding(16.dp)
        ) {




            items(userAportacions) { aportacio ->
                AportacioCard(aportacio = aportacio, onDelete = { viewModel.eliminarAportacio(aportacio) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


