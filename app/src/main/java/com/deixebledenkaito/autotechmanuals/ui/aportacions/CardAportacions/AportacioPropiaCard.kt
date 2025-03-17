package com.deixebledenkaito.autotechmanuals.ui.aportacions.CardAportacions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser

@Composable
fun AportacioPropiaCard(
    aportacio: AportacioUser,
    onDelete: () -> Unit, // Funció per eliminar l'aportació
    onClick: () -> Unit // Funció per navegar a la pantalla de detalls
) {
    AportacioBaseCard(
        aportacio = aportacio,
        onClick = onClick,
        additionalContentPosition = AdditionalContentPosition.BELOW_ALL, // Posició a sota de tot
        additionalContent = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onDelete, modifier = Modifier.width(100.dp).padding(0.dp).height(35.dp)) {
                    Text("Eliminar")
                }
            }
        }
    )
}