package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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


@Composable
fun AportacioCardDetail(
    aportacio: AportacioUser,
    onClick: () -> Unit // Afegir aquest paràmetre
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }, // Afegir el clic aquí
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nom de l'usuari
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start){
                Text(
                    text = aportacio.userName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Data de l'aportació
                Text(text = aportacio.data, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Estrelles
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Estrelles", tint = Color.Yellow)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = aportacio.likes.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))


            // Scroll horitzontal per a múltiples imatges
            if (aportacio.imageUrls.isNotEmpty()) {
                val imageList = aportacio.imageUrls.split(",") // Suport per múltiples imatges
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    items(imageList) { imageUrl ->
                        Image(
                            painter = rememberImagePainter(imageUrl.trim()),
                            contentDescription = "Imatge de l'aportació",
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .padding(end = 6.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Títol de l'aportació
            Text(text = aportacio.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            // Marca i model
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Marca:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = aportacio.manual, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "Model:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = aportacio.model, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(2.dp))

            // Secció per a PDF i Vídeo amb check o creu
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "PDF:", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (!aportacio.pdfUrls.isNullOrEmpty()) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "Disponibilitat PDF",
                    tint = if (!aportacio.pdfUrls.isNullOrEmpty()) Color.Green else Color.Red
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = "Video:", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (!aportacio.videoUrls.isNullOrEmpty()) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "Disponibilitat Vídeo",
                    tint = if (!aportacio.videoUrls.isNullOrEmpty()) Color.Green else Color.Red
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Descripció de l'aportació
            Text(text = "Definició: ${aportacio.descripcio}", style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        }
    }
}