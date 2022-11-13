package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.DetailCategoryDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryService {

    @GET("industry/detail_category")
    suspend fun getAllCategories(): Response<List<DetailCategoryDTO>>
}