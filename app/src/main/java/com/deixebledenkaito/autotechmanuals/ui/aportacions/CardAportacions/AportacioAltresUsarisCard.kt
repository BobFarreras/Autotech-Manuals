package com.deixebledenkaito.autotechmanuals.ui.aportacions.CardAportacions


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser

@Composable
fun AportacioAltresUsuarisCard(
    aportacio: AportacioUser,
    onLike: () -> Unit, // Funció per donar like
    onDislike: () -> Unit, // Funció per donar dislike
    onClick: () -> Unit // Funció per navegar a la pantalla de detalls
) {
    AportacioBaseCard(
        aportacio = aportacio,
        onClick = onClick,
        additionalContentPosition = AdditionalContentPosition.RIGHT_OF_TITLE, // Posició a la dreta del títol
        additionalContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically // Alinea verticalment tots els elements
            ) {
                // Botó de dislike
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Alinea el icono i el text
                    modifier = Modifier.padding(end = 8.dp) // Afegeix un espai entre like i dislike
                ) {
                    IconButton(
                        onClick = onDislike,
                        modifier = Modifier.size(22.dp) // Mida del botó
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nolike),
                            contentDescription = "No Like",
                            modifier = Modifier.size(26.dp)  // Mida de la icona
                        )
                    }
                    Text(
                        text = aportacio.noLikes.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp) // Espai entre la icona i el text
                    )
                }

                // Botó de like
                Row(
                    verticalAlignment = Alignment.CenterVertically // Alinea el icono i el text
                ) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier.size(22.dp) // Mida del botó
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_like),
                            contentDescription = "Like",
                            modifier = Modifier.size(26.dp)  // Mida de la icona
                        )
                    }
                    Text(
                        text = aportacio.likes.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp) // Espai entre la icona i el text
                    )
                }
            }
        }
    )
}