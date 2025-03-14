package com.deixebledenkaito.autotechmanuals.data.service


import com.deixebledenkaito.autotechmanuals.data.repository.ManualRepository
import com.deixebledenkaito.autotechmanuals.domain.Manuals
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import com.deixebledenkaito.autotechmanuals.utils.Result
import javax.inject.Inject


// data/service/ManualService.kt
class ManualService @Inject constructor(
    private val manualRepository: ManualRepository
) {
    // Memòria cau per a manuals
    private var cachedManuals: List<Manuals>? = null
    private var cachedTopManuals: List<String>? = null

    suspend fun totsElsManuals(): Result<List<Manuals>> {
        return try {
            val manuals = manualRepository.getManuals()
                .map { it.toDomain() }
            cachedManuals = manuals
            Result.Success(manuals)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun getTopManuals(): Result<List<String>> {
        return try {
            val topManuals = cachedTopManuals ?: run {
                val topManuals = manualRepository.getTopManuals()
                cachedTopManuals = topManuals
                topManuals
            }
            Result.Success(topManuals)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }


    suspend fun getLastUsedManual(): Result<String?> {
        return try {
            val manualName = manualRepository.getLastUsedManual()
            Result.Success(manualName)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun updateLastUsedManual(manualName: String): Result<Boolean> {
        return try {
            val success = manualRepository.updateLastUsedManual(manualName)
            Result.Success(success)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun getManualByName(manualName: String): Result<Manuals?> {
        return try {
            val manualResponse = manualRepository.getManualByName(manualName)
            val manual = manualResponse?.toDomain()
            Result.Success(manual)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun incrementManualUsage(manualId: String): Result<Unit> {
        return try {
            manualRepository.incrementManualUsage(manualId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }
}