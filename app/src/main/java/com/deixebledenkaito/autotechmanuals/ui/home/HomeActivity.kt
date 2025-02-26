package com.deixebledenkaito.autotechmanuals.ui.home


import ProfileScreen
import android.net.Uri

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import coil.compose.rememberImagePainter

import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.ui.ChangeNavigationBarColor.ChangeNavigationBarColor
import com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen.ModelDetailScreen
import com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen.buttons.btnErrors.ErrorsDelModelScreen
import com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen.buttons.btnManuals.DescarregarManualsScreen
import com.deixebledenkaito.autotechmanuals.ui.ModelDatailScreen.buttons.btnParametres.AjustListScreen

import com.deixebledenkaito.autotechmanuals.ui.aportacions.NovaAportacioScreen
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.BackgroundColor
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.BackgroundTopAddBar
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.title
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
    Log.d("HomeScreen", "Últim manual a la UI: ${lastManual?.nom}")
    val user by viewModel.user.collectAsState()

    var showAddManualDialog by remember { mutableStateOf(false) }

    var showSearchDialog by remember { mutableStateOf(false) } // Estat per al diàleg de cerca
    var searchQuery by remember { mutableStateOf("") } // Estat per al terme de cerca


    val MyCustomTextStyle = TextStyle(
        fontSize = 18.sp, // Mida del text
//        fontWeight = FontWeight.Bold, // Text en negreta
        fontFamily = FontFamily.Default, // Font per defecte
        color = Color.DarkGray , // Color del text
        letterSpacing = 0.5.sp // Espaiat entre lletres
    )
    LaunchedEffect(Unit) {
        viewModel.loadAllData()
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
                        // Text de benvinguda
                        Text("Benvingut, ${user?.name ?: "Usuari"}")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor, // Fons de la TopAppBar
                    titleContentColor = title // Color del text
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logout {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Tancar sessió")
                    }
                }
            )
        },
//        }, bottomBar = {
//            BottomToolbar(navController = navController, showAddManualDialog = {
//                showAddManualDialog = true}, showSearchDialog = {showSearchDialog = true}
//
//            )
//        },
        containerColor = BackgroundColor // Color de fons de tota l'aplicació
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Permet el scroll a tota la pantalla
                .padding(8.dp)
        ) {
            // Mostra l'últim manual utilitzat
            lastManual?.let { manual ->
                Text("Últim manual utilitzat", style = MyCustomTextStyle)

                Spacer(modifier = Modifier.height(8.dp))
                ManualItem(manual = manual, onClick = {
                    navController.navigate("homeManual/${manual.nom}")
                })
                Spacer(modifier = Modifier.height(22.dp))
            }

            // Llista de manuals més populars (horitzontal)
            Text("Manuals més populars", style = MyCustomTextStyle)

            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(topManuals) { manual ->
                    ManualItem(manual = manual, modifier = Modifier.width(200.dp), onClick = {
                        navController.navigate("homeManual/${manual.nom}") // Navegació amb argument
                    })
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(22.dp))

            // Llista de tots els manuals (vertical en graella de 2 columnes)
            Text("Tots els manuals", style = MyCustomTextStyle)

            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnes
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp), // Limita l'alçada màxima
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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

    if (showSearchDialog) {
        SearchDialog(
            onDismiss = { showSearchDialog = false },
            onSearch = { query ->
                navController.navigate("searchResults/$query") // Navega a la pantalla de result
            }
        )
    }
}
@Composable
fun ManualItem(manual: Manuals, modifier: Modifier = Modifier, onClick: () -> Unit) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(4.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),

            horizontalAlignment = Alignment.CenterHorizontally // Centra el contingut horitzontalment
        ) {
            // Imatge del manual
            Image(
                painter = rememberImagePainter(manual.imageUrl),
                contentDescription = manual.nom,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds // Evita que la imatge es talli
            )
            Spacer(modifier = Modifier.height(6.dp))

        }
    }
}


@Composable
fun AddManualDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Uri) -> Unit,
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
            HomeManualScreen(manualName = manualName, navController = navController)
        }
        composable("login") {
            LoginScreen(loginViewModel = loginViewModel)
        }
        composable("searchResults/{searchQuery}") { backStackEntry ->
            val searchQuery = backStackEntry.arguments?.getString("searchQuery") ?: ""
            SearchResultsScreen(
                searchQuery = searchQuery,
                manuals = viewModel.manuals.value,
                navController = navController
            )
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("novaAportacio") { navBackStackEntry ->
            NovaAportacioScreen(navController = navController)
        }
        // Defineix la ruta per a ModelDetailScreen
        composable("modelDetail/{manualId}/{modelId}") { backStackEntry ->
            val manualId = backStackEntry.arguments?.getString("manualId") ?: ""
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            ModelDetailScreen(
                manualId = manualId,
                modelId = modelId,
                navController = navController
            )
        }
        composable("errorsDelModel/{manualId}/{modelId}") { backStackEntry ->
            val manualId = backStackEntry.arguments?.getString("manualId") ?: ""
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            ErrorsDelModelScreen(
                manualId = manualId,
                modelId = modelId,
                navController = navController
            )
        }

        composable("descarregarManuals/{manualId}/{modelId}") { backStackEntry ->
            val manualId = backStackEntry.arguments?.getString("manualId") ?: ""
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            DescarregarManualsScreen(
                manualId = manualId,
                modelId = modelId,
                navController = navController
            )
        }
        composable("parametres") { navBackStackEntry ->
            AjustListScreen()
        }
    }
}
// Funció per mostrar la toolbar inferior
@Composable
fun BottomToolbar(navController: NavController, showAddManualDialog: () -> Unit, showSearchDialog: () -> Unit) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Create,
        BottomNavItem.Profile
    )

    BottomAppBar(
        modifier = Modifier
            .height(90.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),

        containerColor = Color.White, // Fons de la TopAppBar,

        ) {
        items.forEach { item ->
            IconButton(
                onClick = {
                    when (item) {
                        is BottomNavItem.Home -> navController.popBackStack() // tornar enrrera
                        is BottomNavItem.Profile -> navController.navigate("profile")
                        is BottomNavItem.Search -> showSearchDialog()
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



@Composable
fun SearchDialog(
    onDismiss: () -> Unit, // Funció per tancar el diàleg
    onSearch: (String) -> Unit // Funció que s'executa quan es fa la cerca
) {
    var searchQuery by remember { mutableStateOf("") } // Estat per al terme de cerca

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cercar manuals") },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Introdueix el terme de cerca") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSearch(searchQuery) // Executa la cerca amb el terme introduït
                    onDismiss() // Tanca el diàleg
                }
            ) {
                Text("Cercar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel·lar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchQuery: String, // Terme de cerca
    manuals: List<Manuals>, // Llista de manuals
    navController: NavController // Controlador de navegació
) {
    // Filtra els manuals segons el terme de cerca
    val filteredManuals = manuals.filter { manual ->
        manual.nom.contains(searchQuery, ignoreCase = true) ||
                manual.descripcio.contains(searchQuery, ignoreCase = true)
    }

    // Pantalla de resultats
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultats de cerca: $searchQuery") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(filteredManuals) { manual ->
                ManualItem(manual = manual, onClick = {
                    navController.navigate("homeManual/${manual.nom}") // Navegació amb argument
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


