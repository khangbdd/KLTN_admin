package com.example.aposs_admin.network


import com.example.aposs_admin.model.dto.NewProductDTO
import com.example.aposs_admin.model.dto.NewProductImageDTO
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

    suspend fun addNewProduct(newProductDTO: NewProductDTO, accessToken: String?): Response<Long> {
        return productService.createNewProductWithDefaultSet(newProductDTO, accessToken)
    }

    suspend fun addNewProductImage(newProductImageDTO: NewProductImageDTO, accessToken: String?): Response<String> {
        return productService.addNewImage(newProductImageDTO, accessToken)
    }

    suspend fun updateProduct(id:Long, newProductDTO: NewProductDTO, accessToken: String?): Response<Unit> {
        return productService.updateProduct(id, newProductDTO, accessToken)
    }

    suspend fun updateProductImage(id:Long, newProductImageDTO: NewProductImageDTO, accessToken: String?): Response<Unit> {
        return productService.updateProductImage(id, newProductImageDTO, accessToken)
    }
}