package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.autoDespiece

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculViewModel : ViewModel() {
    private val _resultat = MutableLiveData<ResultatCalcul>()
    val resultat: LiveData<ResultatCalcul> get() = _resultat

    fun calcularResultat(
        ampladaMovil: Int,
        alcadaMovil: Int,
        ampladaFix: Int? = null,
        alcadaFix: Int? = null,
        opcio: String,
        cardType: String
    ) {
        val result = when (cardType) {
            "Una Fulla" -> calcularFullesMovils(ampladaMovil, alcadaMovil, opcio, cardType)
            "Una Fulla i Fix" -> calcularMovilsIFixos(ampladaMovil, alcadaMovil, ampladaFix, alcadaFix, opcio,cardType)
            "Dos Fulles" -> calcularFullesMovils(ampladaMovil, alcadaMovil, opcio, cardType)
            "Dos Fulles i dos Fix"-> calcularMovilsIFixos(ampladaMovil, alcadaMovil, ampladaFix, alcadaFix, opcio,cardType)
            // Afegir més casos per a "Dos Fulles" i "Dos Fulles i dos Fix"...
            else -> ResultatCalcul(0, 0, ampladaPinca = 0) // Cas per defecte
        }
        _resultat.value = result
    }

    private var dosFulles = 1
    private fun calcularFullesMovils(amplada: Int, alcada: Int, opcio: String, cardType: String): ResultatCalcul {
        if(cardType == "Dos Fulles" || cardType =="Dos Fulles i dos Fix"){
            dosFulles = 2
        }
        return when (opcio) {
            "Tot emarcat" -> ResultatCalcul(
                ampladaVidre = amplada/dosFulles - 24,
                alcadaVidre = alcada - 85,
                ampladaSocal = amplada/dosFulles  - 60,
                alcadaVertical = alcada,
                ampladaPinca = amplada/dosFulles  - 60
            )
            "Pinça" -> ResultatCalcul(
                ampladaVidre = amplada/dosFulles ,
                alcadaVidre = alcada - 42,
                ampladaPinca = amplada/dosFulles  - 8
            )
            "Pinça i sòcol" -> ResultatCalcul(
                ampladaVidre = amplada/dosFulles ,
                alcadaVidre = alcada - 85,
                ampladaSocal = amplada/dosFulles  - 8,
                ampladaPinca = amplada/dosFulles  - 8
            )
            else -> ResultatCalcul(0, 0, ampladaPinca = 0) // Cas per defecte
        }
    }

    private fun calcularMovilsIFixos(
        ampladaMovil: Int,
        alcadaMovil: Int,
        ampladaFix: Int?,
        alcadaFix: Int?,
        opcio: String,
        cardType: String
    ): ResultatCalcul {
        if(cardType == "Dos Fulles" || cardType =="Dos Fulles i dos Fix"){
            dosFulles = 2
        }
        return when (opcio) {
            "Tot emarcat" -> ResultatCalcul(
                ampladaVidre = ampladaMovil/dosFulles  - 24,
                alcadaVidre = alcadaMovil - 85,
                ampladaSocal = ampladaMovil/dosFulles  - 60,
                alcadaVertical = alcadaMovil,
                ampladaPinca = ampladaMovil/dosFulles  - 60,

                ampladaVidreFix = ampladaFix?.div(dosFulles)?.minus(29),
                alcadaVidreFix = alcadaFix?.minus(43+8+8+2+20),
                ampladaSocalFix = ampladaFix?.div(dosFulles)?.minus(65),
                alcadaVerticalFix = alcadaFix?.minus(18),
                alcadaVerticalGoma = alcadaFix?.minus(5),
                ampladaPincaFix = ampladaFix?.div(dosFulles)?.minus(65),
                guiador = ampladaFix?.div(dosFulles)?.minus(37), // Exemple de càlcul
                UHoritzontal = ampladaFix?.div(dosFulles)?.minus(24), // Exemple de càlcul
                UVertical = alcadaFix?.minus(0) // Exemple de càlcul
            )
            "Pinça" -> ResultatCalcul(
                ampladaVidre = ampladaMovil/dosFulles,
                alcadaVidre = alcadaMovil - 42,
                ampladaPinca = ampladaMovil/dosFulles - 8,

                ampladaVidreFix = ampladaFix?.div(dosFulles),
                alcadaVidreFix = alcadaFix?.minus(28),
                ampladaPincaFix = ampladaFix?.minus(4)
            )
            "Pinça i sòcol" -> ResultatCalcul(
                ampladaVidre = ampladaMovil/dosFulles,
                alcadaVidre = alcadaMovil - 85,
                ampladaSocal = ampladaMovil/dosFulles - 8,
                ampladaPinca = ampladaMovil/dosFulles - 8,

                ampladaVidreFix = ampladaFix?.div(dosFulles),
                alcadaVidreFix = alcadaFix?.minus(8+8+43+20+2),
                ampladaSocalFix = ampladaFix?.div(dosFulles)?.minus(4),
                ampladaPincaFix = ampladaFix?.div(dosFulles)?.minus(4) ,
                guiador = ampladaFix?.div(dosFulles)?.minus(0), // Exemple de càlcul
                UHoritzontal = ampladaFix?.div(dosFulles)?.minus(0), // Exemple de càlcul
            )
            else -> ResultatCalcul(0, 0, ampladaPinca = 0) // Cas per defecte
        }
    }
}

data class ResultatCalcul(
    val ampladaVidre: Int,
    val alcadaVidre: Int,
    val ampladaSocal: Int? = null,
    val alcadaVertical: Int? = null,
    val ampladaPinca: Int,
    val ampladaVidreFix: Int? = null,
    val alcadaVidreFix: Int? = null,

    val ampladaSocalFix: Int? = null,
    val ampladaPincaFix: Int? = null,
    val alcadaVerticalFix: Int? = null,
    val alcadaVerticalGoma: Int? = null,

    val guiador: Int? = null, // Nou camp
    val UHoritzontal: Int? = null, // Nou camp
    val UVertical: Int? = null // Nou camp
)