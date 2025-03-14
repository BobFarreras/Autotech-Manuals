package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.saveRouteButton

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.autotechmanuals.R
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.sharedViewModel.SharedViewModel

@Composable
fun SaveRouteButton(
    sharedViewModel: SharedViewModel,
    onSaveRoute: () -> RutaGuardada,
    modifier: Modifier = Modifier
) {
    // Obtenim el contingut de la ruta actual
    val ruta = remember { onSaveRoute() }
    val rutaContent = remember { ruta.ruta }

    // Estat de la ruta guardada
    var isRouteSaved by remember { mutableStateOf(false) }

    // Verifiquem si la ruta està guardada pel seu contingut
    LaunchedEffect(rutaContent) {
        sharedViewModel.isRutaGuardada(rutaContent)
            .collect { isSaved ->
                isRouteSaved = isSaved
            }
    }

    // Icona segons l'estat de la ruta
    val icon = if (isRouteSaved) {
        painterResource(id = R.drawable.guardat) // Icona quan la ruta està guardada
    } else {
        painterResource(id = R.drawable.noguardat) // Icona quan la ruta no està guardada
    }

    // Botó amb icona
    IconButton(
        onClick = {
            if (!isRouteSaved) {
                sharedViewModel.guardarRuta(ruta) // Guarda la ruta a Firebase
                isRouteSaved = true // Actualitza l'estat localment
            }
        },
        modifier = modifier,
        enabled = !isRouteSaved // Desactiva el botó si la ruta ja està guardada
    ) {
        Image(
            modifier = Modifier.width(24.dp),
            painter = icon,
            contentDescription = if (isRouteSaved) "Ruta guardada" else "Guardar ruta"
        )
    }
}