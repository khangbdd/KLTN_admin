package com.example.aposs_admin.ui_controller.product_detail_fragment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.ProductDetail
import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.ProductDTO
import com.example.aposs_admin.model.dto.ProductDetailDTO
import com.example.aposs_admin.model.dto.ProductImageDTO
import com.example.aposs_admin.network.ProductRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    private var selectedProductId: Long = 0

    private var _selectedProduct = MutableLiveData<ProductDetail>()
    val selectedProduct: LiveData<ProductDetail> get() = _selectedProduct

    private var _selectedProductImages = MutableLiveData<List<Image>>()
    val selectedProductImages: LiveData<List<Image>> get() = _selectedProductImages

//    private val _selectedProductQuantities = MutableLiveData<Int>()
//    val selectedProductQuantities: MutableLiveData<Int> get() = _selectedProductQuantities

    val productDetailLoadingState = MutableLiveData<LoadingStatus>()


    fun setSelectedProductId(id: Long) {
        selectedProductId = id
        if (selectedProductId != (-1).toLong()) {
            loadSelectedProductById(selectedProductId)
            loadListImageByID(selectedProductId)
        }
    }

    private fun mapToHomeProduct(productDTO: ProductDTO): HomeProduct {
        return HomeProduct(
            id = productDTO.id,
            image = Image(productDTO.image),
            name = productDTO.name,
            price = productDTO.price,
            purchased = productDTO.purchased
        )
    }

    private fun mapToProductDetail(productDetailDTO: ProductDetailDTO, id: Long): ProductDetail {
        return ProductDetail(
            id,
            productDetailDTO.name,
            productDetailDTO.price,
            productDetailDTO.purchase,
            true,
            productDetailDTO.description,
            productDetailDTO.quantity,
            productDetailDTO.kindName,
            productDetailDTO.kindId
        )
    }

    private fun loadSelectedProductById(id: Long) {
        productDetailLoadingState.value = LoadingStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productResponse = productRepository.getProductById(id)
                if (productResponse.isSuccessful) {
                    _selectedProduct.postValue(mapToProductDetail(productResponse.body()!!, id))
                    withContext(Dispatchers.Main) {
//                        _selectedProductQuantities.postValue(_selectedProduct.value!!.availableQuantities)
                        productDetailLoadingState.postValue(LoadingStatus.Success)
                    }
                } else {
                    productDetailLoadingState.postValue(LoadingStatus.Fail)
                }
            } catch(e: Exception) {
                if (e is SocketTimeoutException) {
                    loadSelectedProductById(id)
                }
                else e.printStackTrace()
            }
        }
    }

    private fun mapToImage(productImageDTO: ProductImageDTO): Image {
        return Image(productImageDTO.imageUrl)
    }

    private fun loadListImageByID(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productImageResponse = productRepository.getProductImageById(id)
                if (productImageResponse.isSuccessful) {
                    _selectedProductImages.postValue(productImageResponse.body()!!.stream().map {
                        mapToImage(it)
                    }.collect(Collectors.toList()))
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadListImageByID(id)
                } else e.printStackTrace()
            }
        }
    }
}