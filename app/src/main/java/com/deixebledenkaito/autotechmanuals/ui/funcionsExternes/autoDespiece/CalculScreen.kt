package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoDespiece

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun CalculScreen(
    navController: NavController,
    cardType: String?,
    viewModel: CalculViewModel = hiltViewModel()
) {
    // Estats per als valors dels EditText
    var fullaMovilAmplada by remember { mutableStateOf("") }
    var fullaMovilAlcada by remember { mutableStateOf("") }
    var fullaFixAmplada by remember { mutableStateOf("") }
    var fullaFixAlcada by remember { mutableStateOf("") }
    var opcioSeleccionada by remember { mutableStateOf("Tot emarcat") }

    // Observar el resultat del ViewModel
    val resultat by viewModel.resultat.observeAsState()

    var isExpanded by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Botó per desplegar/replegar la part superior
        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isExpanded) "Replegar" else "Desplegar"
            )
        }
        // Mostrar la part superior si està desplegada
        if (isExpanded) {
            // Mostrar els camps segons el tipus de card
            when (cardType) {
                "Una Fulla" , "DosFulles" -> {
                    Text("Mòbil")
                    EditTextCamp(
                        label = "Amplada Fulla Mòbil",
                        value = fullaMovilAmplada,
                        onValueChange = { fullaMovilAmplada = it }
                    )
                    EditTextCamp(
                        label = "Alçada Fulla Mòbil",
                        value = fullaMovilAlcada,
                        onValueChange = { fullaMovilAlcada = it }
                    )
                }
                "Una Fulla i Fix" , "Dos Fulles i dos Fix"-> {
                    Text("Mòbil")
                    EditTextCamp(
                        label = "Amplada Fulla Mòbil",
                        value = fullaMovilAmplada,
                        onValueChange = { fullaMovilAmplada = it }
                    )
                    EditTextCamp(
                        label = "Alçada Fulla Mòbil",
                        value = fullaMovilAlcada,
                        onValueChange = { fullaMovilAlcada = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Fix")
                    EditTextCamp(
                        label = "Amplada Fix",
                        value = fullaFixAmplada,
                        onValueChange = { fullaFixAmplada = it }
                    )
                    EditTextCamp(
                        label = "Alçada Fix",
                        value = fullaFixAlcada,
                        onValueChange = { fullaFixAlcada = it }
                    )
                }
                // Afegir més casos per a "Dos Fulles" i "Dos Fulles i dos Fix"...
            }

            // Selector d'opcions
            Text("Selecciona una opció:", modifier = Modifier.padding(top = 6.dp))
            RadioGroup(
                options = listOf("Pinça", "Pinça i sòcol", "Tot emarcat"),
                selectedOption = opcioSeleccionada,
                onOptionSelected = { opcioSeleccionada = it }
            )

            // Botó per calcular
            Button(
                onClick = {
                    viewModel.calcularResultat(
                        ampladaMovil = fullaMovilAmplada.toIntOrNull() ?: 0,
                        alcadaMovil = fullaMovilAlcada.toIntOrNull() ?: 0,
                        ampladaFix = fullaFixAmplada.toIntOrNull(),
                        alcadaFix = fullaFixAlcada.toIntOrNull(),
                        opcio = opcioSeleccionada,
                        cardType = cardType ?: ""
                    )
                    isExpanded = false // Replegar la part superior després de calcular
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Calcular")
            }
        }


        // Mostrar tots els resultats amb Scroll
        resultat?.let { result ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        var dosUnitats = ""
                        if(cardType == "Dos Fulles" || cardType == "Dos Fulles i dos Fix" ){
                            dosUnitats = "2 unitats"
                        }

                        Text("Vidre Mòbil: $dosUnitats", style = MaterialTheme.typography.titleLarge)
                        Text("Amplada Vidre: ${result.ampladaVidre}", style = MaterialTheme.typography.titleMedium)
                        Text("Alçada Vidre: ${result.alcadaVidre}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Mòbil:$dosUnitats", style = MaterialTheme.typography.titleLarge)
                        if (opcioSeleccionada == "Tot emarcat") {
                            Text("Amplada Sòcol: ${result.ampladaSocal ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            Text("Alçada Vertical: ${result.alcadaVertical ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                        }
                        if (opcioSeleccionada == "Pinça i sòcol") {
                            Text("Amplada Sòcol: ${result.ampladaSocal ?: "N/A"}", style = MaterialTheme.typography.titleMedium)

                        }
                        Text("Amplada Pinça: ${result.ampladaPinca}", style = MaterialTheme.typography.titleMedium)

                        if (cardType == "Una Fulla i Fix") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Vidre Fix:$dosUnitats", style = MaterialTheme.typography.titleLarge)
                            Text("Amplada Vidre Fix: ${result.ampladaVidreFix ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            Text("Alçada Vidre Fix: ${result.alcadaVidreFix ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Fix:$dosUnitats", style = MaterialTheme.typography.titleLarge)
                            Text("Pinça Fix: ${result.ampladaPincaFix ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            Text("Sòcal Fix: ${result.ampladaSocalFix ?: "N/A"}", style = MaterialTheme.typography.titleMedium)

                            if (opcioSeleccionada == "Tot emarcat") {
                                Text("Vertical Radera: ${result.alcadaVerticalFix ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                                Text("Vertical Goma: ${result.alcadaVerticalGoma ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                                Text("Guiador: ${result.guiador ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                                Text("U Horitzontal: ${result.UHoritzontal ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                                Text("U Vertical: ${result.UVertical ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            }
                            if (opcioSeleccionada == "Pinça i sòcol") {
                                Text("Amplada Sòcol Fix: ${result.ampladaSocalFix ?: "N/A"}", style = MaterialTheme.typography.titleMedium)

                            }

                        }
                    }
                }
            }
        }
    }
}
@Composable
fun RadioGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row {
        options.forEach { option ->
            Row(
                modifier = Modifier

                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = option,

                )
            }
        }
    }
}
@Composable
fun EditTextCamp(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}