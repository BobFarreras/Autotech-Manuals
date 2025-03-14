package com.deixebledenkaito.autotechmanuals.data.service

import com.deixebledenkaito.autotechmanuals.data.repository.ErrorRepository
import com.deixebledenkaito.autotechmanuals.domain.ErrorsDelModel
import com.deixebledenkaito.autotechmanuals.utils.Result
import javax.inject.Inject

// data/service/ErrorService.kt
class ErrorService @Inject constructor(
    private val errorRepository: ErrorRepository
) {
    suspend fun buscarErrorPerNumero(manualId: String, modelId: String, numero: String): Result<ErrorsDelModel?> {
        return errorRepository.buscarErrorPerNumero(manualId, modelId, numero)
    }
}