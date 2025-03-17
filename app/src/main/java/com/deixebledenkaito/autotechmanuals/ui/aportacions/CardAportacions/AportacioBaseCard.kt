package com.deixebledenkaito.autotechmanuals.ui.aportacions.CardAportacions


import androidx.compose.foundation.Image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser

import androidx.compose.ui.text.style.TextOverflow

import com.deixebledenkaito.autotechmanuals.R

// Enum per a definir la posició del contingut addicional
enum class AdditionalContentPosition {
    RIGHT_OF_TITLE, // A la dreta del títol
    BELOW_ALL // A sota de tot
}


@Composable
fun AportacioBaseCard(
    aportacio: AportacioUser,
    onClick: () -> Unit,
    additionalContent: @Composable (() -> Unit)? = null,
    additionalContentPosition: AdditionalContentPosition = AdditionalContentPosition.RIGHT_OF_TITLE // Paràmetre nou
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Si el contingut addicional va a la dreta del títol

            Row {
                // Títol de l'aportació
                Text(
                    text = aportacio.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(40.dp))
                // Contingut addicional (botons, etc.)
                if (additionalContentPosition == AdditionalContentPosition.RIGHT_OF_TITLE) {additionalContent?.invoke()}

            }


            Spacer(modifier = Modifier.height(8.dp))

            // Descripció de l'aportació
            Text(
                text = aportacio.descripcio,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Imatges i vídeos (si n'hi ha)
            if (aportacio.imageVideos.isNotEmpty() || !aportacio.pdfUrls.isNullOrEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // Alçada fixa per a tots els elements
                ) {
                    // Afegir imatges i miniatures de vídeos
                    items(aportacio.imageVideos) { imageVideo ->
                        AsyncImage(
                            model = imageVideo.imageUrl,
                            contentDescription = "Imatge o miniatura del vídeo",
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .padding(end = 6.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Afegir PDFs
                    items(aportacio.pdfUrls?.split(",") ?: emptyList()) { pdfUrl ->
                        val trimmedUrl = pdfUrl.trim()
                        if (trimmedUrl.isNotEmpty()) {
                            PdfCard(
                                pdfUrl = trimmedUrl,
                                onClick = {
                                    // Obrir el PDF (pots afegir la lògica aquí)
                                },
                                modifier = Modifier
                                    .width(100.dp) // Mida consistent
                                    .height(100.dp)
                                    .padding(end = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

            }
            Spacer(modifier = Modifier.height(8.dp))
            // Si el contingut addicional va a sota de tot
            if (additionalContentPosition == AdditionalContentPosition.BELOW_ALL) {
                additionalContent?.invoke()
            }

        }
    }
}
@Composable
fun PdfCard(
    pdfUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Afegir un modifier opcional
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo del PDF
            Image(
                painter = painterResource(id = R.drawable.logopdf),
                contentDescription = "Logo PDF",
                modifier = Modifier
                    .size(50.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Nom del PDF
            Text(
                text = pdfUrl.substringAfterLast("/"),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

