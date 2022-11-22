package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.model.dto.NewSubcategoryDTO
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

    suspend fun getAllSubCategoryByCategoryId(categoryId: Long): Response<List<KindDTO>>{
        return kindService.getAllKind(categoryId)
    }

    suspend fun createSubcategory(accessToken: String, subcategory: NewSubcategoryDTO): Response<Unit>{
        return kindService.createSubcategory(accessToken, subcategory)
    }

    suspend fun deleteSubcategory(accessToken: String, id: Long): Response<Unit>{
        return kindService.deleteSubcategory(accessToken, id)
    }

    suspend fun updateSubcategory(accessToken: String, id: Long, newSubcategoryDTO: NewSubcategoryDTO): Response<Unit>{
        return kindService.updateSubcategory(accessToken, id, newSubcategoryDTO)
    }
}