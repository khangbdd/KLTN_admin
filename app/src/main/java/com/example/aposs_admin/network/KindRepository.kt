package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.network.service.KindService
import retrofit2.Response
import retrofit2.create
import javax.inject.Inject

class KindRepository @Inject constructor() {
    private val kindService: KindService by lazy {
        RetrofitInstance.retrofit.create(KindService::class.java)
    }

    suspend fun getAllKind() :Response<List<KindDTO>> {
        return kindService.getAllKind()
    }
}