package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.*
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*


interface ProductAPIService {
    @GET("products")
    suspend fun getProductsAsync(
        @Query("pageNo") pageNo: Int = 1
    ): Response<ProductResponseDTO>

    @GET("products/search")
    suspend fun getProductsByKeywordAsync(
        @Query("keyword") keyword: String = "",
        @Query("pageNo") pageNo: Int = 1,
        @Query("sortBy") sortBy: String = "id",
        @Query("sortDir") sortDir: String = "asc",
    ): Response<ProductResponseDTO>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path(value = "id") id: Long
    ): Response<ProductDetailDTO>

    @GET("products/{id}/images")
    suspend fun getProductImagesById(
        @Path(value = "id") id: Long
    ): Response<List<ProductImageDTO>>

    @GET("products/kind/{id}")
    suspend fun getProductByKindId(
        @Path(value = "id") id: Long
    ): Response<ProductResponseDTO>

    @POST("products/with-default-set")
    suspend fun createNewProductWithDefaultSet(
        @Body newProductDTO: NewProductDTO,
        @Header("Authorization") accessToken: String?
    ): Response<Long>

    @POST("products/image")
    suspend fun addNewImage(
        @Body newProductImageDTO: NewProductImageDTO,
        @Header("Authorization") accessToken: String?
    ): Response<String>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path(value = "id") id: Long,
        @Body newProductDTO: NewProductDTO,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @POST("products/image/{id}")
    suspend fun updateProductImage(
        @Path(value = "id") id: Long,
        @Body newProductImageDTO: NewProductImageDTO,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>
}