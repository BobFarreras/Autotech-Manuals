package com.deixebledenkaito.autotechmanuals.ui.aportacions.aportacioCardDetail



import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.ModelDetailViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deixebledenkaito.autotechmanuals.R


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AportacioCardDetailHome(
    aportacioId: String,
    navController: NavController,
    viewModel: ModelDetailViewModel = hiltViewModel(),
) {

    Log.d("AportacioCardDetailHOme", aportacioId)
    Log.d("AportacioCardDetailHOme", viewModel.aportacions.value.toString())
    // Recuperar l'aportació del ViewModel
    val aportacio = viewModel.aportacions.value.find { it.id == aportacioId }


    // Si no es troba l'aportació, mostrar un missatge d'error
    if (aportacio == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Aportació no trobada", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    // Mostrar les dades de l'aportació
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalls de l'aportació") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back), // Substitueix amb el teu drawable
                            contentDescription = "Tornar",
                            modifier = Modifier.size(24.dp) // Ajusta la mida segons sigui necessari
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            // Mostrar les dades de l'aportació
            Text(text = "Títol: ${aportacio.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Descripció: ${aportacio.descripcio}", style = MaterialTheme.typography.bodyMedium)
            // Afegir més camps segons sigui necessari...
        }
    }
}