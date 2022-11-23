package com.example.aposs_admin.ui_controller.add_product_fragment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.LocalImage
import com.example.aposs_admin.model.dto.*
import com.example.aposs_admin.network.AuthRepository
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
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val kindRepository: KindRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val listDisplayCategories: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listCategories: MutableLiveData<MutableList<DetailCategoryDTO>> = MutableLiveData()
    val listDisplayKind: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listKind: MutableLiveData<MutableList<KindDTO>> = MutableLiveData()
    val listImages: MutableLiveData<ArrayList<LocalImage>> = MutableLiveData()
    val listImagesUrl: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val listName: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData("")
    val price: MutableLiveData<String> = MutableLiveData("")
    val quantity: MutableLiveData<String> = MutableLiveData("")
    val description: MutableLiveData<String> = MutableLiveData("")
    val kind: MutableLiveData<String> = MutableLiveData("")
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
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            loadAllCategories()
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
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
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            loadAllKind()
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
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

    private val numLoaded = AtomicInteger(0)
    fun loadImageToFirebase(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            listImages.value!!.stream().forEach { it ->
                val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                val imageName = "${LocalDateTime.now()}.${it.type.toString()}"
                listImageUrl.add("https://firebasestorage.googleapis.com/v0/b/kltn-admin-5643f.appspot.com/o/${imageName}?alt=media")
                val imageRef = storageRef.child(imageName)
                val uploadTask = imageRef.putFile(Uri.parse(it.imageUri.toString()))
                uploadTask.addOnFailureListener { e ->
                    e.printStackTrace()
                    status.postValue(LoadingStatus.Fail)
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        numLoaded.incrementAndGet()
                        if (numLoaded.get() == listImages.value!!.size) {
                            requestSaveImageUrl(productId)
                        }
                    }
                }
            }
        }
    }

    private val listImageUrl = mutableListOf<String>()
    private val numLoadedDatabase = AtomicInteger(0)
    fun requestSaveImageUrl(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            var index = 1
            for (url in listImageUrl) {
                val newProductImageDTO = createNewProductImageDTO(productId, url, index)
                try {
                    val response = productRepository.addNewProductImage(
                        newProductImageDTO,
                        "${token?.tokenType.toString()} ${token?.accessToken.toString()}"
                    )
                    if (response.isSuccessful) {
                        numLoadedDatabase.incrementAndGet()
                        if (numLoadedDatabase.get() == listImageUrl.size) {
                            status.postValue(LoadingStatus.Success)
                        }
                    } else {
                        if (response.code() == 401) {
                            if (authRepository.loadNewAccessTokenSuccess()) {
                                createNewProductImageDTO(productId, url, index)
                            } else {
                                status.postValue(LoadingStatus.Fail)
                            }
                        } else {
                            status.postValue(LoadingStatus.Fail)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                index+= 1
            }
        }
    }

    val status: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun requestCreateNewProduct() {
        status.postValue(LoadingStatus.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newProductDTO = createNewProductDTO()
                val responseProduct = productRepository.addNewProduct(
                    newProductDTO,
                    "${token?.tokenType.toString()} ${token?.accessToken.toString()}"
                )
                if (responseProduct.isSuccessful) {
                    val newProductId = responseProduct.body()
                    if (newProductId != null) {
                        loadImageToFirebase(newProductId)
                    }
                } else {
                    if (responseProduct.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            requestCreateNewProduct()
                        } else {
                            status.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        status.postValue(LoadingStatus.Fail)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    requestCreateNewProduct()
                } else {
                    e.printStackTrace()
                }
                status.postValue(LoadingStatus.Fail)
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

    private fun createNewProductImageDTO(
        productId: Long,
        url: String,
        priority: Int
    ): NewProductImageDTO {
        return NewProductImageDTO(
            imageUrl = url,
            priority = priority,
            productId = productId,
        )
    }

    fun deleteImage(position: Int) {
        val listTempt = listImages.value
        listTempt!!.removeAt(position)
        listImages.value =listTempt
    }
}