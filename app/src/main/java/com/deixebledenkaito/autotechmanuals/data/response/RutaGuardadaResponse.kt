package com.deixebledenkaito.autotechmanuals.data.response


import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada


data class RutaGuardadaResponse (
    val id: String ="",
    val nom: String="", // Nom de la ruta
    val ruta: String="", // Ruta de navegació (per exemple, "homeManual/nomManual")
    val dataGuardat: Long = 0L // Timestamp de quan es va guardar
) {


    fun toDomain(): RutaGuardada {
        return RutaGuardada(
            id = id,
            nom = nom,
            ruta = ruta,
            dataGuardat = dataGuardat

        )
    }
}