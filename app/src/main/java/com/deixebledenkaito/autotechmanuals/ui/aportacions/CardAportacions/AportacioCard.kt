package com.deixebledenkaito.autotechmanuals.ui.aportacions.CardAportacions


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import com.deixebledenkaito.autotechmanuals.R

//000000000000000000000000000 CARD DEL PERFIL DE L'USUARI 00000000000000000000000000000000000000'
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Icona per a "No Like"
                Image(
                    painter = painterResource(id = R.drawable.ic_nolike), // Carrega el drawable
                    contentDescription = "No Like", // Descripció per accessibilitat
                    modifier = Modifier.size(24.dp), // Defineix la mida de la icona

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = aportacio.noLikes.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Icona per a "Like"
                Image(
                    painter = painterResource(id = R.drawable.ic_like), // Carrega el drawable
                    contentDescription = "Like", // Descripció per accessibilitat
                    modifier = Modifier.size(24.dp), // Defineix la mida de la icona

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = aportacio.likes.toString(), // Assegura't que aquest camp existeix a AportacioUser
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

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
                                .padding(end = 8.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(text = aportacio.title, style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Marca:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = aportacio.manual, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "Model:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = aportacio.model, style = MaterialTheme.typography.titleSmall)
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

                Text(text = "Video:", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (!aportacio.videoUrls.isNullOrEmpty()) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "Disponibilitat Vídeo",
                    tint = if (!aportacio.videoUrls.isNullOrEmpty()) Color.Green else Color.Red
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(text = "Definició: ${aportacio.descripcio}", style = MaterialTheme.typography.bodyMedium, maxLines = 4,  modifier = Modifier.width(200.dp))
                // Botó d'eliminar
                Row(modifier = Modifier.fillMaxWidth() .padding(top = 30.dp), horizontalArrangement = Arrangement.End ) {
                    androidx.compose.material3.Button(
                        onClick = { onDelete(aportacio) }
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}