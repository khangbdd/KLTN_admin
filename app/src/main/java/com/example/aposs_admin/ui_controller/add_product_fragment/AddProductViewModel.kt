package com.example.aposs_admin.ui_controller.add_product_fragment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.LocalImage
import com.example.aposs_admin.model.dto.*
import com.example.aposs_admin.network.CategoryRepository
import com.example.aposs_admin.network.KindRepository
import com.example.aposs_admin.network.ProductRepository
import com.example.aposs_admin.util.LoadingStatus
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val kindRepository: KindRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    val listDisplayCategories: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listCategories: MutableLiveData<MutableList<DetailCategoryDTO>> = MutableLiveData()
    val listDisplayKind: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listKind: MutableLiveData<MutableList<KindDTO>> = MutableLiveData()
    val listImages: MutableLiveData<ArrayList<LocalImage>> = MutableLiveData()
    val listImagesUrl: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val listName: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData()
    val price: MutableLiveData<String> = MutableLiveData()
    val quantity: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()
    val kind: MutableLiveData<String> = MutableLiveData()
    var token: TokenDTO? = null

    init {
        listImages.value = arrayListOf()
        listImagesUrl.value = arrayListOf()
        listName.value = arrayListOf()
        listDisplayCategories.value = mutableListOf()
        listCategories.value = mutableListOf()
        listDisplayKind.value = mutableListOf()
        listKind.value = mutableListOf()
        loadAllCategories()
        loadAllKind()
    }

    fun addImages(uri: Uri, type: String?) {
        val listTempt = listImages.value
        listTempt!!.add(LocalImage(uri, type))
        listImages.value = listTempt
    }

    private fun loadAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = categoryRepository.getAllCategory()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        listCategories.postValue(response.body()!! as MutableList<DetailCategoryDTO>?)
                        val listTempt = mutableListOf<String>()
                        response.body()!!.stream().forEach {
                            listTempt.add("${it.id} - ${it.name}")
                        }
                        listDisplayCategories.postValue(listTempt)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadAllCategories()
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadAllKind() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = kindRepository.getAllKind()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        listKind.postValue(response.body()!! as MutableList<KindDTO>?)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadAllCategories()
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    fun filterKind(categoryId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            val listTempt = mutableListOf<String>()
            listKind.value!!.stream().forEach {
                if (it.category == categoryId) {
                    listTempt.add("${it.id} - ${it.name}")
                }
            }
            listDisplayKind.postValue(listTempt)
        }
    }

    fun loadImageToFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            var index = 1
            listImages.value!!.stream().forEach { it ->
                val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                val imageName = "${name.value}-${index}.${it.type.toString()}"
                val imageRef = storageRef.child(imageName)
                val uploadTask = imageRef.putFile(Uri.parse(it.imageUri.toString()))
                uploadTask.addOnFailureListener { e ->
                    e.printStackTrace()
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        listImagesUrl.value?.add("https://firebasestorage.googleapis.com/v0/b/kltn-admin-5643f.appspot.com/o/${imageName}?alt=media")
                    }
                }
                index++
            }
        }
    }

    fun requestSaveImageUrl(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val scope = this
            var index = 0
            for(url in listImagesUrl.value!!){
                val newProductImageDTO = createNewProductImageDTO(productId, url, index)
                try {
                    Log.i("TTTTTTTTTT", "loading")
                    val response = productRepository.addNewProductImage(newProductImageDTO, "${token?.tokenType.toString()} ${token?.accessToken.toString()}")
                    Log.i("TTTTTTTTTT", "done 1")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            status.postValue(LoadingStatus.Success)
        }
    }

    val status : MutableLiveData<LoadingStatus> = MutableLiveData()
    fun requestCreateNewProduct() {
        status.postValue(LoadingStatus.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i("TTTTTTTTTT", "start")
                val newProductDTO = createNewProductDTO()
                val responseProduct = productRepository.addNewProduct(newProductDTO, "${token?.tokenType.toString()} ${token?.accessToken.toString()}")
                Log.i("TTTTTTTTTT", responseProduct.body().toString())
                if (responseProduct.isSuccessful) {
                    val newProductId = responseProduct.body()
                    if (newProductId != null) {
                        loadImageToFirebase()
                        requestSaveImageUrl(newProductId)
                        Log.i("TTTTTTTTTT", "almost")
                    }
                }
            } catch (e: Exception) {
                Log.i("TTTTTTTTTT", "fail")
                if(e is SocketTimeoutException) {
                    requestCreateNewProduct()
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun createNewProductDTO(): NewProductDTO {
        var priceInt: Int = 0
        if (price.value.toString().toIntOrNull() != null) {
            priceInt = price.value.toString().toIntOrNull()!!
        }
        var quantityInt: Int = 0
        if (quantity.value.toString().toIntOrNull() != null) {
            quantityInt = quantity.value.toString().toIntOrNull()!!
        }
        var kindLong: Long = 0L
        if (kind.value.toString()[0].toString().toLongOrNull() != null) {
            kindLong = kind.value.toString()[0].toString().toLongOrNull()!!
        }
        return NewProductDTO(
            description = description.value,
            name = name.value,
            price = priceInt,
            quantity = quantityInt,
            kindId = kindLong
        )
    }

    private fun createNewProductImageDTO(productId: Long, url: String, priority: Int): NewProductImageDTO {
        return NewProductImageDTO (
            productId = productId,
            imageUrl = url,
            priority = priority
        )
    }
}