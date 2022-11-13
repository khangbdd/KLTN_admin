package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.KindDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KindService {
    @GET("kind")
    suspend fun getAllKind(@Query("categoryId") categoryId: Long = -1): Response<List<KindDTO>>
}