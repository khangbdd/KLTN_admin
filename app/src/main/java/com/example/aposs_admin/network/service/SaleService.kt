package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.SaleDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SaleService {

    @GET("sale/all/product/{productId}")
    suspend fun loadSaleDataByProduct(
        @Path(value = "productId") productId: Long,
    ): Response<List<SaleDTO>>

    @GET("sale/all/subcategory/{subcategoryId}")
    suspend fun loadSaleDataBySubcategory(
        @Path(value = "subcategoryId") subcategoryId: Long,
    ): Response<List<SaleDTO>>

    @GET("sale/product/{productId}")
    suspend fun loadSaleDataByProductBetweenDate(
        @Path(value = "productId") productId: Long,
        @Query(value = "from") fromDateString: String,
        @Query(value = "to") toDateString: String
    ): Response<List<SaleDTO>>

    @GET("sale/subcategory/{subcategoryId}")
    suspend fun loadSaleDataBySubcategoryBetweenDate(
        @Path(value = "subcategoryId") subcategoryId: Long,
        @Query(value = "from") fromDateString: String,
        @Query(value = "to") toDateString: String
    ): Response<List<SaleDTO>>
}