package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnParametres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deixebledenkaito.autotechmanuals.domain.Ajust




@Composable
fun AjustListScreen() {


    // Llista d'ajustos (MENU 1)
    val ajustosMenu1 = listOf(
        Ajust("u0", "Velocitat d'obertura", "03 04…10 12…20 25…50 80 cm/s"),
        Ajust("uC", "Velocitat de tancament", "03 04…10 12…20 25…50 80 cm/s"),
        Ajust("SO", "Velocitat de frenada a l'obertura", "00 01…07 cm/s"),
        Ajust("SC", "Velocitat de frenada al tancament", "00 01…07 cm/s"),
        Ajust("oH", "Durada de la retenció d'obertura estiu", "00 01…10 12…20 25…60 s"),
        Ajust("Or", "Durada de la retenció d'obertura hivern", "00 01…10 12…20 25…60 s"),
        Ajust("oS", "Durada de la retenció d'obertura contacte autoritzat (KB)", "00 01…10 12…20 25…60 s"),
        Ajust("od", "Prolongació dinàmica de la durada de retenció d'obertura", "00 no / 01 sí"),
        Ajust("b0", "Acceleració", "1…10 12…20 25…40 × 10 cm/s²"),
        Ajust("ur", "Activació velocitat de frenada", "00 Sense velocitat reduïda / 01 Velocitat de frenada abans de l'estat obert i tancat / 02 Velocitat de frenada abans de l'estat obert / 03 Velocitat de frenada abans de l'estat tancat"),
        Ajust("F0", "Força estàtica a l'obrir", "01 10 15 20 25 × 10 N"),
        Ajust("FC", "Força estàtica a tancar", "01 10 15 20 25 × 10 N"),
        Ajust("CF", "Força de tancament mantinguda", "00 01…10 12…20 25…50 60…90"),
        Ajust("CL", "Límit d'inversió", "01 02…06…10 12…20 25 mm"),
        Ajust("nE", "Canviar al 2n menú", "–"))
    val ajustosMenu2 = listOf(
        Ajust("s1", "Seguretat 1 tipus de contacte (borne SIS1)", "00 No utilitzat / 02 Contacte NC"),
        Ajust("F1", "Seguretat 1 Funció (borne SIS1)", "01 SIS rev / 02 SIS i KI / 03 SIS i KA / 04 SIS lent / 05 SIO parada / 06 SIO lent / 07 SIO Break-Out"),
        Ajust("S2", "Seguretat 2 tipus de contacte (borne SIS2)", "00 No utilitzat / 02 Contacte NC"),
        Ajust("F2", "Seguretat 2 Funció (borne SIS2)", "01 SIS rev / 02 SIS i KI / 03 SIS i KA / 04 SIS lent / 05 SIO parada / 06 SIO lent / 07 SIO Break-Out"),
        Ajust("S3", "Seguretat 3 tipus de contacte (borne SIO1)", "00 No utilitzat / 02 Contacte NC"),
        Ajust("F3", "Seguretat 3 Funció (borne SIO1)", "01 SIS rev / 02 SIS i KI / 03 SIS i KA / 04 SIS lent / 05 SIO parada / 06 SIO lent / 07 SIO Break-Out"),
        Ajust("S4", "Seguretat 4 tipus de contacte (borne SIO2)", "00 No utilitzat / 02 Contacte NC"),
        Ajust("F4", "Seguretat 4 Funció (borne SIO2)", "01 SIS rev / 02 SIS i KI / 03 SIS i KA / 04 SIS lent / 05 SIO parada / 06 SIO lent / 07 SIO Break-Out"),
        Ajust("Cb", "Dispositiu de contacte Autoritzat tipus de contacte", "00 No utilitzat / 01 Contacte NA"),
        Ajust("Ci", "Contacte Interior tipus de contacte", "00 No utilitzat / 01 Contacte NA / 02 Contacte NC / 03 Tensió / 04 Freqüència"),
        Ajust("Ai", "Contacte Interior retard de comandament", "00 01 … 10 s"),
        Ajust("nt", "KI nit temps d'espera", "00 01 … 10 12 … 20 25 … 90 s"),
        Ajust("Co", "Contacte exterior tipus de contacte", "00 No utilitzat / 01 Contacte NA / 02 Contacte NC / 04 Freqüència"),
        Ajust("AA", "Contacte exterior retard d'activació", "00 10 s"),
        Ajust("E1", "Entrada 1 parametritzable", "00 No utilitzat / 02 Mode de funcionament OFF NO / 03 Estiu NO / 04 Hivern NO / 05 Sabotatge NC / 06 Farmàcia NO / 08 Accionament P-KI NO / 09 Accionament P-KA NO / 10 Funció biestable NO / 11 Punt de contacte, tancament després d'Os NO / 13 Tecla de reset NO / 14 Doble pulsació NO / 21 Control WC NO"),
        Ajust("E2", "Entrada 2 parametritzable", "00 No utilitzat / 01 MPS / 02 Mode de funcionament OFF NO / 03 Estiu NO / 04 Hivern NO / 05 Sabotatge NC / 06 Farmàcia NO / 07 Bloqueig d'emergència NO / 08 Accionament P-KI NO / 09 Accionament P-KA NO / 10 Funció biestable NO / 11 Punt de contacte, tancament després d'Os NO / 12 STOP 12k / 13 Tecla de reset NO / 14 Doble pulsació NO / 20 Desbloqueig manual NO / 21 Control WC NO / 23 STOP 20k NC / 24 STOP contacte NO NO / 25 STOP contacte NC NC"),
        Ajust("E3", "Entrada 3 parametritzable", "00 No utilitzat / 01 MPS / 02 Mode de funcionament OFF NO / 03 Estiu NO / 04 Hivern NO / 05 Sabotatge NC / 06 Farmàcia NO / 07 Bloqueig d'emergència NO / 08 Accionament P-KI NO / 09 Accionament P-KA NO / 10 Funció biestable NO / 11 Punt de contacte, tancament després d'Os NO / 12 STOP 12k / 13 Tecla de reset NO / 14 Doble pulsació NO / 21 Control WC NO / 23 STOP 20k NC / 24 STOP contacte NO NO / 25 STOP contacte NC NC"),
        Ajust("A1", "Sortida 1 parametritzable", "00 No utilitzat / 01 Timbre / 02 Averia contacte NO / 03 Averia contacte NC / 04 Averia per MPS / 05 Senyal d'avís / 06 Fren del motor / 07 Ventilador del motor / 08 Tancat i bloquejat / 09 Tancat / 10 No tancat / 11 Obert / 12 OFF / 13 NA / 14 LS / 15 AU / 16 DO / 17 Control de la il·luminació / 18 Obre en esclusa / 19 No obre en esclusa / 20 Manteniment vençut / 23 Error desbloqueig manual / 24 Error WC límit de temps"),
        Ajust("A2", "Sortida 2 parametritzable", "00 No utilitzat / 01 Timbre / 02 Averia contacte NO / 03 Averia contacte NC / 04 Averia per MPS / 05 Senyal d'avís / 06 Fren del motor / 07 Ventilador del motor / 08 Tancat i bloquejat / 09 Tancat / 10 No tancat / 11 Obert / 12 OFF / 13 NA / 14 LS / 15 AU / 16 DO / 17 Control de la il·luminació / 18 Obre en esclusa / 19 No obre en esclusa / 20 Manteniment vençut / 23 Error desbloqueig manual / 24 Error WC Timeout"),
        Ajust("nE", "Canviar al tercer menú", "–")
    )
    val ajustosMenu3 = listOf(
        Ajust("Er", "Errors actualment pendients", "CE Esborrar la memòria d'errors"),
        Ajust("oE", "Errors antics (10 últims fallos)", "CE Esborrar la memòria d'errors"),
        Ajust("di", "Diagnòstic", "r0 Sense bloqueig / r1 Amb bloqueig / A0 Sense acumulador / A1 Amb acumulador / xx Pes de fulla (x 100 kg) / yy + pes de fulla (x kg) / S8 ECO Mode"),
        Ajust("S8", "ECO Mode", "00 Apagat / 01 On"),
        Ajust("sT", "Tipus de control", "00 DCU1-NT / 01 DCU1-RD / 02 DCU1-T30 / 20 DCU1-2M_NT / 21 DCU1-2M-DUO / 22 DCU1-2M-LL / 23 DCU1-2M-RWS"),
        Ajust("SA", "Temps de funcionament (indicació 6 dígits)", "Co Nombre de cicles / 100 / Ho Hores de funcionament / 4 / Fo Nombre d'autotests"),
        Ajust("CS", "Apagar LED service", "cs Es mostra breument per a la seva identificació"),
        Ajust("CP", "Restaurar ajust de fàbrica", "–"),
        Ajust("Fr/Fo", "Alliberar la connexió del motor / Connectar el motor", "–"),
        Ajust("SP", "Idioma", "00 Alemany / 01 English / 02 Français / 04 Italià / 05 Espanyol"),
        Ajust("LE", "Iniciar aprenentatge", "–"),
        Ajust("EP", "Versió de software", "p.ej. St, 40 per DCU1-NT V4.0"),
        Ajust("nE", "Canviar al quart menú", "–")
    )
    val ajustosMenu4 = listOf(
        Ajust("At", "Tipus d'automatisme", "00 Desconegut / 01 Slimdrive SC / 02 Slimdrive SF / 03 Slimdrive SL / 04 Slimdrive SL NT / 05 Slimdrive SL BO / 06 Slimdrive SL CO48 / 07 Slimdrive SLT / 08 Slimdrive SLV / 09 ECdrive / 10 ECdrive CO48 / 11 Powerdrive PL / 12 ECdrive BO / 13 TSA 360NT BO / 14 Powerdrive PL CO48 / 15 Slimdrive SL NT-CO48 / 16 Slimdrive SLT-CO48 / 17 ECdrive T2 / 18 ECdrive T2-CO48"),
        Ajust("EF", "Nombre de fulles de porta", "01 Tancar a un costat / 02 Tancar centrat"),
        Ajust("AC", "Falla d'alimentació en LS, AU o DO", "00 Sense funció / 01 Obrir / 02 Tancar / 03 Funcionament per acumulador 30 min, després obrir / 04 Funcionament per acumulador 30 min, després tancar"),
        Ajust("Eo", "Obertura en cas d'errors", "00 La porta queda tancada / 01 La porta obre en cas d'errors"),
        Ajust("rt", "Tipus de pestell", "00 Sense pestell / 01 Biestable / 02 Motoritzat / 03 Corrent de treball / 04 Normalment obert / 05 Lock A (pestell pic de lloro)"),
        Ajust("SL", "Esclusa, cortavent", "00 Master / 01 Esclusa Slave / 02 Cortavent Slave"),
        Ajust("CA", "Direcció CAN (sistema d'edificis GEZE)", "00 Sense funció / 01 … 63 Direcció de bus GEZE / 69 GEZE loT Protocol")
    )





    Spacer(modifier = Modifier.height(32.dp))
    LazyColumn(

        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Afegeix un text com a capçalera
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Ocupa tot l'amplada
                    .padding(vertical = 8.dp), // Espai vertical
                contentAlignment = Alignment.Center // Centra el contingut
            ) {
                Text(
                    text = "Menu 1",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        items(ajustosMenu1) { ajust ->
            AjustItem(ajust)
        }
        // Afegeix un text com a capçalera
        // Afegeix un text com a capçalera
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Ocupa tot l'amplada
                    .padding(vertical = 8.dp), // Espai vertical
                contentAlignment = Alignment.Center // Centra el contingut
            ) {
                Text(
                    text = "Menu 2",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        items(ajustosMenu2) { ajust ->
            AjustItem(ajust)
        }
        // Afegeix un text com a capçalera
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Ocupa tot l'amplada
                    .padding(vertical = 8.dp), // Espai vertical
                contentAlignment = Alignment.Center // Centra el contingut
            ) {
                Text(
                    text = "Menu 3",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        items(ajustosMenu3) { ajust ->
            AjustItem(ajust)
        }

        // Afegeix un text com a capçalera
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Ocupa tot l'amplada
                    .padding(vertical = 8.dp), // Espai vertical
                contentAlignment = Alignment.Center // Centra el contingut
            ) {
                Text(
                    text = "Menu 4",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        items(ajustosMenu4) { ajust ->
            AjustItem(ajust)
        }

    }
}

@Composable
fun AjustItem(ajust: Ajust) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = ajust.indicacio,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = ajust.explicacio,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            text = ajust.valorsAjust,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
    }
}