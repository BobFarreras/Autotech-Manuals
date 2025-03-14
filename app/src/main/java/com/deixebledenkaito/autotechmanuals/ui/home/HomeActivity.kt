package com.deixebledenkaito.autotechmanuals.ui.home
import ProfileScreen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.ModelDetailScreen
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnErrors.ErrorsDelModelScreen
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnManuals.DescarregarManualsScreen
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnParametres.AjustListScreen
import com.deixebledenkaito.autotechmanuals.ui.aportacions.NovaAportacioScreen
import com.deixebledenkaito.autotechmanuals.ui.aportacions.aportacioCardDetail.AportacioCardDetailHome
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoMidesImg.CameraSizeDetectorApp
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.BackgroundColor
import com.deixebledenkaito.autotechmanuals.ui.home.ui.theme.title
import com.deixebledenkaito.autotechmanuals.ui.homeManuals.HomeManualScreen
import com.deixebledenkaito.autotechmanuals.ui.auth.login.LoginScreen
import com.deixebledenkaito.autotechmanuals.ui.auth.login.LoginViewModel
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnCarpinteria.CarpinteriaScreen
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnCarpinteria.ESGScreen
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnConeccions.ManualEcdriveConeccionsScreen
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoDespiece.AutoDespieceHome
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoDespiece.CalculScreen
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.sharedViewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date


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
fun HomeScreen(viewModel: HomeViewModel, navController: NavController,  sharedViewModel: SharedViewModel = hiltViewModel()) {
    val manuals by viewModel.manuals.collectAsState()
    val topManuals by viewModel.topManuals.collectAsState()
    val lastManual by viewModel.lastManual.collectAsState()
    val user by viewModel.user.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Observar l'estat de les rutes guardades
    val rutesGuardades by sharedViewModel.rutesGuardades.collectAsState()
    Log.d("HomeScreen", "Rutes guardades: ${rutesGuardades.size}")

    val MyCustomTextStyle = TextStyle(
        fontSize = 18.sp, // Mida del text
//        fontWeight = FontWeight.Bold, // Text en negreta
        fontFamily = FontFamily.Default, // Font per defecte
        color = Color.DarkGray , // Color del text
        letterSpacing = 0.5.sp // Espaiat entre lletres
    )

    LaunchedEffect(Unit) {
        viewModel.loadAllData()

        // Carregar les rutes guardades quan es carrega la pantalla
        sharedViewModel.carregarRutesGuardades()

    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        user?.profileImageUrl?.let { imageUrl ->
                            // Imatge de perfil
                            AsyncImage(
                                model = imageUrl, // URL de la imatge
                                contentDescription = "Imatge de perfil",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Benvingut, ${user?.name ?: "Usuari"}")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = title
                ),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Més opcions")
                    }

                    AnimatedVisibility(
                        visible = showMenu,
                        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                            initialScale = 0.5f,
                            transformOrigin = TransformOrigin(1f, 0f),
                            animationSpec = tween(200)
                        ),
                        exit = fadeOut(animationSpec = tween(300)) + scaleOut(
                            targetScale = 0.5f,
                            transformOrigin = TransformOrigin(1f, 0f),
                            animationSpec = tween(200)
                        ),
                        modifier = Modifier.background(Color.White)
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_logout),
                                            contentDescription = "Tancar sessió",
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Tancar sessió", fontSize = 16.sp)
                                    }
                                },
                                onClick = {
                                    viewModel.logout {
                                        navController.navigate("login") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_profile),
                                            contentDescription = "Perfil",
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Perfil", fontSize = 16.sp)
                                    }
                                },
                                onClick = {
                                    navController.navigate("profile")
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_settings),
                                            contentDescription = "Configuració",
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "CameraIA", fontSize = 16.sp)
                                    }
                                },
                                onClick = {
                                    navController.navigate("camera")
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_search),
                                            contentDescription = "Buscar",
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Buscar", fontSize = 16.sp)
                                    }
                                },
                                onClick = {
                                    showSearchDialog = true
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(6.dp)

        ) {
            // Secció de rutes guardades
            item {
                Text("Rutes Guardades", style = MyCustomTextStyle)
                Spacer(modifier = Modifier.height(4.dp))
                if (rutesGuardades.isEmpty()) {
                    Text(
                        text = "No tens cap ruta guardada.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)

                    )
                } else {
                    LazyRow {
                        items(
                            items = rutesGuardades,
                            key = { ruta -> ruta.id } // Utilitza un identificador únic per a cada ruta
                        ) { ruta ->
                            RutaGuardadaCard(
                                ruta = ruta,
                                onClick = {
                                    navController.navigate(ruta.ruta)
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mostra l'últim manual utilitzat
            lastManual?.let { manual ->
                item {
                    Log.d("UltimManual", lastManual.toString())
                    Text("Últim manual utilitzat", style = MyCustomTextStyle)
                    Spacer(modifier = Modifier.height(4.dp))
                    ManualItem(manual = manual, onClick = {
                        navController.navigate("homeManual/${manual.nom}")
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Llista de manuals més populars (horitzontal)
            item {
                Text("Manuals més populars", style = MyCustomTextStyle)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow {
                    items(topManuals) { manual ->
                        ManualItem(manual = manual, modifier = Modifier.width(200.dp), onClick = {
                            navController.navigate("homeManual/${manual.nom}")
                        })
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Llista de tots els manuals (vertical en graella de 2 columnes)
            item {
                Text("Tots els manuals", style = MyCustomTextStyle)
                Spacer(modifier = Modifier.height(4.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 800.dp),
                    contentPadding = PaddingValues(2.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(manuals) { manual ->
                        ManualItem(manual = manual, onClick = {
                            viewModel.incrementManualUsage(manual.id)
                            viewModel.updateLastUsedManual(manual.nom)
                            navController.navigate("homeManual/${manual.nom}")
                        })
                    }
                }
            }
        }
    }

    if (showSearchDialog) {
        SearchDialog(
            onDismiss = { showSearchDialog = false },
            onSearch = { query ->
                navController.navigate("searchResults/$query")
            }
        )
    }
}
@Composable
fun ManualItem(manual: Manuals, modifier: Modifier = Modifier, onClick: () -> Unit) {

    Card(
        modifier = modifier
            .width(125.dp)
            .height(100.dp)
            .padding(4.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),


            horizontalAlignment = Alignment.CenterHorizontally // Centra el contingut horitzontalment
        ) {
            // Imatge del manual (des de res/drawable o una per defecte)
            Image(
                painter = painterResource(id = manual.imageResId),
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
        composable("camera") {
            CameraSizeDetectorApp(navController = navController)
        }
        composable("novaAportacio") { navBackStackEntry ->
            NovaAportacioScreen(navController = navController)
        }
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
                modelId = modelId

            )
        }
        composable("parametres") { navBackStackEntry ->
            AjustListScreen()
        }

        composable("aportacioDetailHome/{aportacioId}") { backStackEntry ->
            val aportacioId = backStackEntry.arguments?.getString("aportacioId") ?: ""
            AportacioCardDetailHome(aportacioId = aportacioId, navController = navController)
        }
        composable("Coneccions") { backStackEntry ->
            ManualEcdriveConeccionsScreen()
        }
        composable("carpinteria/{manualId}/{modelId}") {backStackEntry ->
            val manualId = backStackEntry.arguments?.getString("manualId") ?: ""
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            CarpinteriaScreen(
                navController = navController ,
                manualId = manualId,
                modelId = modelId,
                )
        }
        composable("esg/{manualId}/{modelId}") {backStackEntry ->
            val manualId = backStackEntry.arguments?.getString("manualId") ?: ""
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            val esg = "ESG"
            ESGScreen(
                manualId = manualId,
                modelId = modelId,
                carpinteriaID = esg,
                navController = navController)
        }

        composable("autoDespieceHome") { navBackStackEntry ->
            AutoDespieceHome(  navController = navController)
        }
        composable("calcul/{cardType}") { backStackEntry ->
            val cardType = backStackEntry.arguments?.getString("cardType")
            CalculScreen(navController = navController, cardType = cardType)
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
                    // Botó per tornar enrere amb la teva icona personalitzada
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back), // Icona personalitzada
                            contentDescription = "Tornar"
                        )
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

fun getImageResIdFromManualName(manualName: String): Int {
    // Neteja el nom del manual
    val cleanedName = manualName
        .replace(" ", "_") // Reemplaça espais amb _
        .replace("[^a-zA-Z0-9_]".toRegex(), "") // Elimina caràcters especials
        .lowercase() // Converteix a minúscules

    // Retorna l'identificador de recurs (R.drawable.*)
    return try {
        // Utilitza la reflexió per obtenir l'ID del recurs
        val resId = R.drawable::class.java.getField(cleanedName).getInt(null)
        resId
    } catch (e: Exception) {
        // Si no es troba la imatge, retorna una imatge per defecte
        R.drawable.ic_gallery
    }
}

@Composable
fun RutaGuardadaCard(ruta: RutaGuardada, onClick: () -> Unit) {



    Card(
        modifier = Modifier
            .width(150.dp)
            .height(80.dp)
            .clickable { onClick() }
            .background(Color.White)
            .padding(4.dp)
            .border(
                width = 0.5.dp, // Gruix del borde
                color = Color.Gray, // Color del borde
                shape = RoundedCornerShape(8.dp) // Forma del borde
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White), // Defineix el color de fons aquí
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = ruta.nom, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(4.dp), textAlign = TextAlign.Center)
            Text(
                text = SimpleDateFormat("dd/MM/yyyy").format(Date(ruta.dataGuardat)),
                style = TextStyle(color = Color. Gray,fontSize = 10.sp,fontFamily = FontFamily. Monospace),
                modifier = Modifier.padding(6.dp), textAlign = TextAlign.Center)
        }
    }
}




