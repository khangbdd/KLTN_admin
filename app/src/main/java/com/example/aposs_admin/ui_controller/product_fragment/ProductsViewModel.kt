package com.example.aposs_admin.ui_controller.product_fragment

import android.icu.text.StringSearch
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.ProductResponseDTO
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.dto.ProductDTO
import com.example.aposs_admin.network.ProductRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products: MutableLiveData<ArrayList<HomeProduct>> = MutableLiveData(arrayListOf())
    val products: LiveData<ArrayList<HomeProduct>>
        get() = _products
    val displayProducts: MutableLiveData<ArrayList<HomeProduct>> = MutableLiveData(arrayListOf())
    private var currentPage = 1
    private var isLastPage = false
    private var _status = MutableLiveData<LoadingStatus>()
    val status: LiveData<LoadingStatus> get() = _status
    val searchText: MutableLiveData<String> = MutableLiveData("")

    init {
        loadProducts()
    }

    fun loadProducts() {
        if (!isLastPage && _status.value != LoadingStatus.Loading) {
            _status.postValue(LoadingStatus.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                val getProductDeferred =
                    productRepository.productService.getProductsAsync(currentPage)
                try {
                    if (getProductDeferred.isSuccessful) {
                        val productResponseDTO: ProductResponseDTO = getProductDeferred.body()!!
                        val productsInCurrentPage = productResponseDTO.content.stream()
                            .map { productDTO -> convertProductDTOtoHomeProduct(productDTO) }
                            .collect(
                                Collectors.toList()
                            )
                        withContext(Dispatchers.Main) {
                            _products.postValue(
                                ArrayList(
                                    concatenate(
                                        _products.value!!,
                                        productsInCurrentPage
                                    )
                                )
                            )
                            displayProducts.postValue(
                                ArrayList(
                                    concatenate(
                                        _products.value!!,
                                        productsInCurrentPage
                                    )
                                )
                            )
                        }
                        if (productResponseDTO.last) {
                            isLastPage = true
                        } else {
                            currentPage++
                        }
                        _status.postValue(LoadingStatus.Success)
                    }
                } catch (e: Exception) {
                    if (e is SocketTimeoutException) {
                        loadProducts()
                    }
                    Log.d("exception", e.toString())
                    _status.postValue(LoadingStatus.Fail)
                }
            }
        }
    }

    private fun convertProductDTOtoHomeProduct(productDTO: ProductDTO): HomeProduct {
        return HomeProduct(
            id = productDTO.id,
            image = Image(productDTO.image),
            name = productDTO.name,
            price = productDTO.price,
            purchased = productDTO.purchased
        )
    }

    private fun <T> concatenate(vararg lists: List<T>): List<T> {
        val result: MutableList<T> = ArrayList()
        for (list in lists) {
            result.addAll(list)
        }
        return result
    }

    fun filter() {
        val listTempt = arrayListOf<HomeProduct>()
        products.value?.stream()?.forEach { product ->
            searchText.value?.let { searchText ->
                if (product.name.contains(searchText, true)) {
                    listTempt.add(product)
                }
            }
        }
        displayProducts.value = listTempt
    }
}