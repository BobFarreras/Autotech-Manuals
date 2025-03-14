package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoDespiece

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deixebledenkaito.autotechmanuals.R

@Composable
fun AutoDespieceHome(
    navController: NavController,
) {
    // Llista de dades per a les Cards
    val cardData = listOf(
        CardData(R.drawable.ic_launcher_foreground, "Una Fulla"),
        CardData(R.drawable.ic_launcher_foreground, "Una Fulla i Fix"),
        CardData(R.drawable.ic_launcher_foreground, "Dos Fulles"),
        CardData(R.drawable.ic_launcher_foreground, "Dos Fulles i dos Fix")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Primera fila de Cards (2 Cards horitzontals)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Mostrem les dues primeres Cards
            for (i in 0..1) {
                ItemCard(
                    imageResId = cardData[i].imageResId,
                    title = cardData[i].title,
                    onClick = {
                        // Navegar a la pantalla de càlcul amb el tipus de card
                        navController.navigate("calcul/${cardData[i].title}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                )
            }
        }

        // Segona fila de Cards (2 Cards horitzontals)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Mostrem les dues últimes Cards
            for (i in 2..3) {
                ItemCard(
                    imageResId = cardData[i].imageResId,
                    title = cardData[i].title,
                    onClick = {
                        // Navegar a la pantalla de càlcul amb el tipus de card
                        navController.navigate("calcul/${cardData[i].title}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                )
            }
        }
    }
}

// Classe de dades per a les Cards
data class CardData(
    val imageResId: Int,
    val title: String
)

@Composable
fun ItemCard(
    imageResId: Int, // ID de la imatge (pots canviar-ho per un `Painter` si prefereixes)
    title: String,    // Títol de la Card
    onClick: () -> Unit, // Acció al fer clic
    modifier: Modifier = Modifier // Modificador per personalitzar la Card
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imatge a dalt
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Títol a sota
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}