package com.example.aposs_admin.network


import com.example.aposs_admin.model.dto.ProductImageDTO
import com.example.aposs_admin.model.dto.ProductDetailDTO
import com.example.aposs_admin.network.service.ProductAPIService
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor() {
     val productService: ProductAPIService by lazy {
        RetrofitInstance.retrofit.create(ProductAPIService::class.java)
    }

//    suspend fun getProductsByKeywordAsync(
//        keyword: String = "",
//        pageNo: Int = 1,
//        sortBy: String = "id",
//        sortDir: String = "asc",
//    ): Deferred<ProductResponseDTO> {
//        return productService.getProductsByKeywordAsync(keyword, pageNo, sortBy, sortDir)
//    }
//
//    suspend fun getProductsAsync(pageNo: Int): Deferred<ProductResponseDTO> {
//        return productService.getProductsAsync(pageNo)
//    }

    suspend fun getProductById(id: Long): Response<ProductDetailDTO> {
        return productService.getProductById(id)
    }

    suspend fun getProductImageById(id: Long): Response<List<ProductImageDTO>> {
        return productService.getProductImagesById(id)
    }
}