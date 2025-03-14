package com.deixebledenkaito.autotechmanuals.data.service


import com.deixebledenkaito.autotechmanuals.data.repository.AportacioRepository
import com.deixebledenkaito.autotechmanuals.data.repository.ModelRepository
import com.deixebledenkaito.autotechmanuals.domain.AportacioUser
import com.deixebledenkaito.autotechmanuals.domain.Model
import com.deixebledenkaito.autotechmanuals.utils.AuthErrorType
import javax.inject.Inject
import com.deixebledenkaito.autotechmanuals.utils.Result


class ModelService @Inject constructor(
    private val modelRepository: ModelRepository,
    private val aportacioRepository: AportacioRepository
) {
    // Obtenir un model per ID
    suspend fun getModelById(manualId: String, modelId: String): Result<Model> {
        return try {
            val model = modelRepository.getModelById(manualId, modelId)
            Result.Success(model)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    // Obtenir les aportacions d'un model
    suspend fun getAportacionsForModel(manualId: String, modelId: String): Result<List<AportacioUser>> {
        return try {
            val aportacions = aportacioRepository.getAportacionsForModel(manualId, modelId)
            Result.Success(aportacions)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconegut", AuthErrorType.INVALID_CODE)
        }
    }

    suspend fun getModelsForManual(manualId: String): Result<List<Model>> {
        return try {
            val models = modelRepository.getModelsForManual(manualId)
            Result.Success(models)
        } catch (e: Exception) {
            Result.Error("Error obtenint models: ${e.message}", AuthErrorType.INVALID_CODE)
        }

    }


}