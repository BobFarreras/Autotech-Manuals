package com.deixebledenkaito.autotechmanuals.ui.dintreDelModel.buttons.btnConeccions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deixebledenkaito.autotechmanuals.R
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale


// Estructura de dades per a una pàgina
data class PageData(

    val subtitle: String? = null,
    val content: List<String>,
    val imageResId: Int? = null
)

// Llista de pàgines
val pages = listOf(
    // Pàgina 10
    PageData(

        subtitle = "Sensor de seguridad “Cierre”",
        content = listOf(
            "4.2 Sensor de infrarrojos activos y detector de movimiento por radar autocontrolado GC 363 SF",
            "Altura de montaje de máx. 3500 mm",
            "• GC 363 SF negro, nº de mat. 151239.",
            "• GC 363 SF según RAL, núm. de mat. 151240.",
            "• El GC 363 SF contiene una cortina de luz con control de infrarrojos activo y un detector de movimiento por radar direccional sensible autocontrolado, con salida de frecuencia (100 Hz).",
            "• Observar las instrucciones de montaje GC 363 SF.",
            "• Ajustar el campo de registro y la sensibilidad del detector de movimiento por radar según AutSchR:",
            "• Campo de registro = AA anchura de abertura × 1,5 m, velocidad por encima de 10 cm/s."
        ),
        imageResId = R.drawable.gc363sf // Substituir per la teva imatge
    ),

    // Pàgina 11
    PageData(

        subtitle = "Sensor de seguridad “Cierre”",
        content = listOf(
            "4.3 Sensor de infrarrojos activos GC 339",
            "• GC 339 negro, núm. de mat. 151251.",
            "• GC 339 conforme a RAL, núm. de mat. 151252.",
            "• Observar las instrucciones de montaje GC 339.",
            "• Aislar los cables no utilizados (WH, YE)."
        ),
        imageResId = R.drawable.gc339// Substituir per la teva imatge
    ),

    // Pàgina 20
    PageData(

        subtitle = "Conexión en serie de los sensores de seguridad",
        content = listOf(
            "7.1 Sensor de seguridad “Cierre” (puertas estándar)",
            "7.1.1 Sensor de infrarrojos activos GC 339 con sensor de accionamiento/sensor de seguridad GC 363 R",
            "GC 339 y GC 363 R satisfacen los requisitos conforme a EN 16005 y DIN 18650."
        ),
        imageResId = R.drawable.gc365r // Substituir per la teva imatge
    ),

    // Pàgina 23
    PageData(

        subtitle = "Conexión en serie de los sensores de seguridad",
        content = listOf(
            "7.2 Sensor de seguridad “Cierre“ (puertas FR)",
            "7.2.1 Sensor de infrarrojos activos GC 339 con sensor de accionamiento/sensor de seguridad GC 363 SF",
            "GC 339 y GC 363 SF satisfacen los requisitos conforme a EN 16005 y DIN 18650."
        ),
        imageResId = R.drawable.gc363sf // Substituir per la teva imatge
    ),

    // Pàgina 27
    PageData(

        subtitle = "Contacto autorizado",
        content = listOf(
            "8.1 Contacto llave",
            "• Contacto llave SCT, unipolar, UP (montaje empotrado), AS 500 sin semicilindros de perfil, núm. de mat. 117996",
            "• Accesorios:",
            "• Semicilindro de perfil, núm. de mat. 090176",
            "• Contacto auxiliar, núm. Contacto adicional, Núm. de mat. 024467 (el contacto adicional no se ha previsto como contacto Sabotage, sino para la autorización del DPS o TPS)"
        ),
        imageResId = R.drawable.contacteobrir // Substituir per la teva imatge
    ),

    // Pàgina 30
    PageData(

        subtitle = "Contacto exterior",
        content = listOf(
            "10.1 detector de movimiento por radar GC 302 R",
            "• Véase Capítulo 9.1.1 detector de movimiento por radar GC 302 R."
        ),
        imageResId = R.drawable.gc302r // Substituir per la teva imatge
    ),

    // Pàgina 35
    PageData(

        subtitle = "Esclusa, cortaviento",
        content = listOf(
            "13 Esclusa, cortaviento",
            "• Dos puertas correderas utilizan el mismo selector de funciones.",
            "• El selector de funciones visualiza sólo los avisos de error del primer mando.",
            "• Esclusa: Una puerta se abre sólo cuando la otra está cerrada.",
            "• Cortavientos: ambas puertas funcionan en el mismo modo de funcionamiento.",
            "• No conectar el borne 2. El selector de funciones está conectado al primer mando."
        ),
        imageResId = R.drawable.dosselectors // Substituir per la teva imatge
    ),

    // Pàgina 36
    PageData(

        subtitle = "Modo de funcionamiento",
        content = listOf(
            "14.1 Selector de funciones mecánico",
            "El LED luce en el selector de funciones mecánico después de que transcurra el intervalo del service o en caso de fallo.",
            "• MPS, AS 500, núm. de mat. 113226",
            "• MPS-ST, con llave, AS 500, núm. de mat. 113227",
            "• Accesorios:",
            "• Tapa de montaje un puesto, AS 500, núm. de mat. 120503",
            "• Modos de funcionamiento: OFF, Na, ls, aU Invierno, D O, aU Verano"
        ),
        imageResId = R.drawable.displayfuncions // Substituir per la teva imatge
    ),

    // Pàgina 38
    PageData(

        subtitle = "Modo de funcionamiento",
        content = listOf(
            "14.5 Función de reseteo (DPS con tecla OFF, TPS)",
            "En el modo de funcionamiento OFF se puede activar el reinicio del software accionando simultáneamente las teclas ▲ y ▼. El accionamiento reacciona como tras la conexión de la tensión de red y se ejecuta una inicialización. La configuración de los parámetros no se modifica."
        )
    ),

    // Pàgina 46
    PageData(

        subtitle = "Puesta en servicio y mantenimiento",
        content = listOf(
            "21.2.2 Puesta en servicio con DPS",
            "CUIDADO",
            "¡Riesgo de lesiones mediante hojas de puerta que abren durante la puesta en servicio!",
            "• Desconectar todos los dispositivos de seguridad.",
            "• Desalojar la vía de desplazamiento.",
            "• Si no se ha montado todavía, conectar el display programador.",
            "• Un selector de funcionamiento o un selector de funciones mecánico no perturba la puesta en servicio con el display programador."
        )
    )
)

@Composable
fun ManualEcdriveConeccionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Iterem sobre la llista de pàgines
        pages.forEach { page ->
            Page(page)
        }
    }
}

@Composable
fun Page(page: PageData) {
    Spacer(modifier = Modifier.width(8.dp))
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        // Subtítol (si existeix)
        page.subtitle?.let {
            Text(
                text = it,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Contingut
        page.content.forEach { text ->
            Text(
                text = text,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

// Imatge (si existeix)
        page.imageResId?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 4.dp),
                contentScale = ContentScale.FillBounds // Escala la imatge per omplir l'espai, sense mantenir la proporció
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}