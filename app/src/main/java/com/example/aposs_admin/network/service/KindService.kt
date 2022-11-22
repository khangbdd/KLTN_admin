package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.model.dto.NewSubcategoryDTO
import retrofit2.Response
import retrofit2.http.*

interface KindService {
    @GET("kind")
    suspend fun getAllKind(@Query("categoryId") categoryId: Long = -1): Response<List<KindDTO>>

    @POST("kind")
    suspend fun createSubcategory(
        @Header("Authorization") accessToken: String,
        @Body subcategory: NewSubcategoryDTO
    ): Response<Unit>

    @DELETE("kind/{id}")
    suspend fun deleteSubcategory(
        @Header("Authorization") accessToken: String,
        @Path(value = "id") id: Long
    ): Response<Unit>

    @PUT("kind/{id}")
    suspend fun updateSubcategory(
        @Header("Authorization") accessToken: String,
        @Path(value = "id") id: Long,
        @Body subcategory: NewSubcategoryDTO
    ):Response<Unit>
}