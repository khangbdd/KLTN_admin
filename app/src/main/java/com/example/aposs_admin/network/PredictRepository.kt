package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.PredictionInfo
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.model.dto.UpdatePredictDTO
import com.example.aposs_admin.network.service.PredictService
import retrofit2.Response
import javax.inject.Inject

class PredictRepository @Inject constructor() {
    private val predictService: PredictService by lazy {
        RetrofitInstance.retrofit.create(PredictService::class.java)
    }

    suspend fun getLimit(frequency: String, kindId: Long, productId: Long?): Response<Int> {
        return predictService.getLimit(frequency, kindId, productId)
    }

    suspend fun createPredict(newPrediction: PredictionInfo, accessToken: String?): Response<Unit> {
        return predictService.createPredict(newPrediction, accessToken)
    }

    suspend fun getAllPredict(): Response<List<PredictionRecordDTO>> {
        return predictService.getAllPredict()
    }

    suspend fun getDetailPredict(predictID: Long): Response<PredictionDetailDTO> {
        return predictService.getDetailPredict(predictID)
    }

    suspend fun deletePredict(predictID: Long, accessToken: String?): Response<Unit> {
        return predictService.deletePredict(predictID, accessToken)
    }

    suspend fun updatePredict(updatePredictDTO: UpdatePredictDTO ,accessToken: String?): Response<Unit> {4
        return predictService.updatePredict(updatePredictDTO, accessToken)
    }
}