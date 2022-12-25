package com.example.aposs_admin.ui_controller.list_product_fragment

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.dto.ProductDTO
import com.example.aposs_admin.network.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class ListProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {

    val searchText: MutableLiveData<String> = MutableLiveData("")
    val listDisplay: MutableLiveData<MutableList<HomeProduct>> = MutableLiveData(
        mutableListOf()
    )
    private val listOfAllProductOfKind: MutableLiveData<MutableList<HomeProduct>> = MutableLiveData(
        mutableListOf()
    )


    private fun filter(text: String) {
            listDisplay.postValue(listOfAllProductOfKind.value!!.stream().filter {
                it.name.contains(text)
            }.collect(Collectors.toList()))
    }

    fun observeSearch(owner: LifecycleOwner) {
        searchText.observe(owner) {
            filter(it)
        }
    }

    fun loadList(kindId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = productRepository.productService.getProductByKindId(kindId)
                if (response.isSuccessful) {
                    listOfAllProductOfKind.postValue(response.body()?.content?.stream()?.map{
                        convertProductDTOtoHomeProduct(it)
                    }?.collect(Collectors.toList()) ?: mutableListOf())
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadList(kindId)
                } else {
                    e.printStackTrace()
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
}