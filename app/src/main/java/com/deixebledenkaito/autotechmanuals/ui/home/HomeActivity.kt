package com.deixebledenkaito.autotechmanuals.ui.home


import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter

import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.ui.homeManuals.HomeManualScreen
import com.deixebledenkaito.autotechmanuals.ui.login.LoginScreen
import com.deixebledenkaito.autotechmanuals.ui.login.LoginViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            AppNavigation()



        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val manuals by viewModel.manuals.collectAsState()
    val topManuals by viewModel.topManuals.collectAsState()
    val lastManual by viewModel.lastManual.collectAsState()
    val user by viewModel.user.collectAsState()

    var showAddManualDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadManuals()
        viewModel.loadTopManuals()
        viewModel.loadLastManual()
        viewModel.loadUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Benvingut, ${user?.name ?: "Usuari"}") },
                actions = {
                    IconButton(onClick = { showAddManualDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Afegir manual")
                    }
                    IconButton(onClick = {
                        viewModel.logout {
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Tancar sessió")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Permet el scroll a tota la pantalla
                .padding(8.dp)
        ) {
            // Últim manual utilitzat
            lastManual?.let { manual ->
                Text("Últim manual utilitzat", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ManualItem(manual = manual, onClick = {
                    navController.navigate("homeManual/${manual.nom}") // Navegació amb argument
                })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Llista de manuals més populars (horitzontal)
            Text("Manuals més populars", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(topManuals) { manual ->
                    ManualItem(manual = manual,modifier = Modifier.width(200.dp), onClick = {
                        navController.navigate("homeManual/${manual.nom}") // Navegació amb argument
                    })
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Llista de tots els manuals (vertical en graella de 2 columnes)
            Text("Tots els manuals", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnes
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp), // Limita l'alçada màxima
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(manuals) { manual ->
                    ManualItem(manual = manual, onClick = {
                        viewModel.incrementManualUsage(manual.id) // Incrementar l'ús del manual
                        viewModel.updateLastUsedManual(manual.nom) // Actualitzar l'últim manual utilitz
                        navController.navigate("homeManual/${manual.nom}") // Navegació amb argument

                    })
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
fun ManualItem(manual: Manuals, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(245.dp)
            .padding(8.dp)
            .clickable { onClick() }, // Afegir el modificador clickable
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Imatge del manual
            Image(
                painter = rememberImagePainter(manual.imageUrl),
                contentDescription = manual.nom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }

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
                Button(onClick = { launcher.launch("image/*") }) {
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: HomeViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable("homeManual/{manualName}") { backStackEntry ->
            val manualName = backStackEntry.arguments?.getString("manualName") ?: ""
            HomeManualScreen(manualName = manualName,navController = navController)
        }
        composable("login") {
            LoginScreen(loginViewModel = loginViewModel)
        }
    }

    // Definim les rutes de la toolbar inferior
    sealed class BottomNavItem(
        val route: String,
        val icon: ImageVector,
        val label: String
    ) {
        object Home : BottomNavItem("home", Icons.Default.Home, "Home")
        object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
        object Search : BottomNavItem("search", Icons.Default.Search, "Cercar")
        object Create : BottomNavItem("create", Icons.Default.Add, "Crear")
    }

    // Funció per mostrar la toolbar inferior
    @Composable
    fun BottomToolbar(navController: NavController, showAddManualDialog: () -> Unit) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Create,
            BottomNavItem.Profile
        )

        BottomAppBar(
            modifier = Modifier.height(56.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            items.forEach { item ->
                IconButton(
                    onClick = {
                        when (item) {
                            is BottomNavItem.Home -> navController.navigate(item.route)
                            is BottomNavItem.Profile -> navController.navigate(item.route)
                            is BottomNavItem.Search -> navController.navigate(item.route)
                            is BottomNavItem.Create -> showAddManualDialog()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                }
            }
        }
    }
}

