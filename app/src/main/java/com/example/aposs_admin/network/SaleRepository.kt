package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.SaleDTO
import com.example.aposs_admin.network.service.ProductAPIService
import com.example.aposs_admin.network.service.SaleService
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

class SaleRepository @Inject constructor() {

    private val saleService: SaleService by lazy {
        RetrofitInstance.retrofit.create(SaleService::class.java)
    }

    suspend fun loadSaleDataByProduct(
        productId: Long,
        frequency: String,
    ): Response<List<SaleDTO>> {
        return saleService.loadSaleDataByProduct(productId, frequency)
    }

    suspend fun loadSaleDataBySubcategory(
        subcategoryId: Long,
        frequency: String,
    ): Response<List<SaleDTO>> {
        return saleService.loadSaleDataBySubcategory(subcategoryId, frequency)
    }

    suspend fun loadSaleDataByProductBetweenDate(
        productId: Long,
        fromDateString: String,
        toDateString: String
    ): Response<List<SaleDTO>> {
        return saleService.loadSaleDataByProductBetweenDate(productId, fromDateString, toDateString)
    }

    suspend fun loadSaleDataBySubcategoryBetweenDate(
       subcategoryId: Long,
       fromDateString: String,
       toDateString: String
    ): Response<List<SaleDTO>> {
        return saleService.loadSaleDataBySubcategoryBetweenDate(subcategoryId, fromDateString, toDateString)
    }
}