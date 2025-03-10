package com.deixebledenkaito.autotechmanuals.domain

data class RutaGuardada(
    val id: String="",
    val nom: String="", // Nom de la ruta
    val ruta: String="", // Ruta de navegació (per exemple, "homeManual/nomManual")
    val dataGuardat: Long =0L // Timestamp de quan es va guardar
) {



    fun toFirestore(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nom" to nom,
            "ruta" to ruta,
            "dataGuardat" to dataGuardat
        )
    }
}