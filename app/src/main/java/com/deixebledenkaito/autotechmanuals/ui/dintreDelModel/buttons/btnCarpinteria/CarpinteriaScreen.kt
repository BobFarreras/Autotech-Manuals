package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnCarpinteria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun CarpinteriaScreen(
    navController: NavController,
    manualId: String,
    modelId: String,) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("esg/$manualId/$modelId")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("ESG")
        }
        Button(
            onClick = { /* Navegar a la pantalla ISO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("ISO")
        }
    }
}