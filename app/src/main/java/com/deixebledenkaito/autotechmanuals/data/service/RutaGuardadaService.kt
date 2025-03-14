package com.deixebledenkaito.autotechmanuals.data.service

import com.deixebledenkaito.autotechmanuals.data.repository.RutaGuardadaRepository
import com.deixebledenkaito.autotechmanuals.domain.RutaGuardada
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.deixebledenkaito.autotechmanuals.utils.Result
import javax.inject.Inject

class RutaGuardadaService @Inject constructor(
    private val rutaGuardadaRepository: RutaGuardadaRepository
) {
    suspend fun obtenirRutesGuardades(userId: String): Result<List<RutaGuardada>> {
        return rutaGuardadaRepository.obtenirRutesGuardades(userId)
    }

    suspend fun guardarRuta(userId: String, ruta: RutaGuardada): Result<Boolean> {
        return try {
            rutaGuardadaRepository.guardarRuta(userId, ruta)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }
    // Funció per verificar si una ruta està guardada pel seu contingut
    suspend fun isRutaGuardada(userId: String, rutaContent: String): Result<Boolean> {
        return try {
            val isGuardada = rutaGuardadaRepository.isRutaGuardada(userId, rutaContent)
            Result.Success(isGuardada)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }
}