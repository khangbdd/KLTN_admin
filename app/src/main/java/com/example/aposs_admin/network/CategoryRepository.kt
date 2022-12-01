package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.NewCategory
import com.example.aposs_admin.network.service.AuthService
import com.example.aposs_admin.network.service.CategoryService
import retrofit2.Response
import javax.inject.Inject

class CategoryRepository @Inject constructor() {
    private val categoryService: CategoryService by lazy {
        RetrofitInstance.retrofit.create(CategoryService::class.java)
    }

    suspend fun getAllCategory() : Response<List<DetailCategoryDTO>>{
        return categoryService.getAllCategories()
    }

    suspend fun addNewCategory(accessToken: String,newCategory: NewCategory): Response<Unit>{
        return categoryService.addNewCategory(accessToken, newCategory)
    }

    suspend fun deleteCategory(accessToken: String, id: Long): Response<Unit>{
        return categoryService.deleteCategory(accessToken, id)
    }
}