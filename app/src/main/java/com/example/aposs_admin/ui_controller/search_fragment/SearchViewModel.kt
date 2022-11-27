package com.example.aposs_admin.ui_controller.search_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.dto.ProductDTO
import com.example.aposs_admin.model.dto.ProductResponseDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.ProductRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val displayProducts: MutableLiveData<ArrayList<HomeProduct>> = MutableLiveData(arrayListOf())
    val searchText: MutableLiveData<String> = MutableLiveData("")

    private var isLastPage = false
    private var currentPage = 1;

    private var _status = MutableLiveData<LoadingStatus>()
    val status: LiveData<LoadingStatus> get() = _status

    init {
        displayProducts.value = arrayListOf()
        loadListForDisplay();
    }

    fun loadListForDisplay() {
        if (!isLastPage) {
            _status.postValue(LoadingStatus.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                val response = productRepository.productService.getProductsByKeywordAsync(
                    searchText.value!!,
                    currentPage
                )
                try {
                    if (response.isSuccessful) {
                        val productResponseDTO = response.body()
                        val lstResultCurrentPage = productResponseDTO?.content?.stream()?.map {
                            convertProductDTOtoHomeProduct(it)
                        }?.collect(Collectors.toList())
                        displayProducts.postValue(arrayListOf())
                        displayProducts.postValue(ArrayList(
                            concatenate(
                                displayProducts.value!!,
                                lstResultCurrentPage ?: arrayListOf()
                            )
                        ))
                        _status.postValue(LoadingStatus.Success)
                        if (productResponseDTO != null) {
                            if (productResponseDTO.last) {
                                isLastPage = true
                            } else {
                                currentPage++
                            }
                        }
                    } else {
                        if (response.code() == 401) {
                            if (authRepository.loadNewAccessTokenSuccess()) {
                                loadListForDisplay()
                            } else {
                                _status.postValue(LoadingStatus.Fail)
                            }
                        } else {
                            _status.postValue(LoadingStatus.Fail)
                        }
                    }
                } catch (e: Exception) {
                    if (e is SocketTimeoutException) {
                        loadListForDisplay()
                    } else {
                        Log.e("Exception", e.toString())
                        _status.postValue(LoadingStatus.Fail)
                    }
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

    fun onSearchTextChange() {
        isLastPage = false
        currentPage = 1
        displayProducts.value = arrayListOf()
        loadListForDisplay()
    }

}