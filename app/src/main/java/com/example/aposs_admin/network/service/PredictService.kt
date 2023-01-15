package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.PredictionInfo
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.model.dto.UpdatePredictDTO
import retrofit2.Response
import retrofit2.http.*

interface PredictService {

    @GET("prediction/max-horizon")
    suspend fun getLimit(
        @Query("frequency") frequency: String,
        @Query("subcategoryId") subcategoryId: Long,
        @Query("productId") productId: Long? = null,
    ): Response<Int>

    @POST("prediction")
    suspend fun createPredict(
        @Body prediction: PredictionInfo,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @GET("prediction/all?isActiveOnly=true")
    suspend fun getAllPredict(): Response<List<PredictionRecordDTO>>

    @GET("prediction/{id}")
    suspend fun getDetailPredict(
        @Path(value = "id") predictId: Long
    ): Response<PredictionDetailDTO>

    @DELETE("prediction/{id}")
    suspend fun deletePredict(
        @Path(value = "id") predictId: Long,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @PUT("prediction")
    suspend fun updatePredict(
        @Body updatePredictDTO: UpdatePredictDTO,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>
}