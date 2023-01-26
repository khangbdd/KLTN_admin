package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.NewCategory
import com.example.aposs_admin.model.dto.NewSubcategoryDTO
import retrofit2.Response
import retrofit2.http.*

interface CategoryService {

    @GET("industry/detail_category")
    suspend fun getAllCategories(): Response<List<DetailCategoryDTO>>

    @POST("industry")
    suspend fun addNewCategory(
        @Header("Authorization") accessToken: String,
        @Body newCategory: NewCategory
    ): Response<Unit>

    @PUT("industry")
    suspend fun updateCategory(
        @Header("Authorization") accessToken: String,
        @Body newCategory: NewCategory
    ): Response<Unit>

    @DELETE("industry/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") accessToken: String,
        @Path(value = "id") id: Long
    ): Response<Unit>
}