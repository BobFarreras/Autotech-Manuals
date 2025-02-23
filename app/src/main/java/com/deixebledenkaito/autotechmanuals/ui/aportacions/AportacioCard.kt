package com.deixebledenkaito.autotechmanuals.ui.aportacions


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser




import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close


@Composable
fun AportacioCard(
    aportacio: AportacioUser,
    onDelete: (AportacioUser) -> Unit // Funció de callback per eliminar
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(text = aportacio.data, style = MaterialTheme.typography.bodyMedium)
                // Estrelles
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Estrelles", tint = Color.Yellow)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = aportacio.stars.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Scroll horitzontal per a múltiples imatges
            if (aportacio.imageUrls.isNotEmpty()) {
                val imageList = aportacio.imageUrls.split(",") // Suport per múltiples imatges
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    items(imageList) { imageUrl ->
                        Image(
                            painter = rememberImagePainter(imageUrl.trim()),
                            contentDescription = "Imatge de l'aportació",
                            modifier = Modifier
                                .width(150.dp)
                                .height(150.dp)
                                .padding(end = 8.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = aportacio.title, style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "MARCA:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = aportacio.manual, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "MODEL:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = aportacio.model, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Secció per a PDF i Vídeo amb check o creu
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "PDF:", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (!aportacio.pdfUrls.isNullOrEmpty()) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "Disponibilitat PDF",
                    tint = if (!aportacio.pdfUrls.isNullOrEmpty()) Color.Green else Color.Red
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = "VIDEO:", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (!aportacio.videoUrls.isNullOrEmpty()) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "Disponibilitat Vídeo",
                    tint = if (!aportacio.videoUrls.isNullOrEmpty()) Color.Green else Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Definició: ${aportacio.descripcio}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))
            // Botó d'eliminar
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                androidx.compose.material3.Button(
                    onClick = { onDelete(aportacio) }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}