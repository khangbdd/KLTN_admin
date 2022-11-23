package com.example.aposs_admin.ui_controller.edit_product_fragment

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.LocalImage
import com.example.aposs_admin.model.ProductDetail
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
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val kindRepository: KindRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var selectedProduct: ProductDetail? = null
    val listDisplayCategories: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listCategories: MutableLiveData<MutableList<DetailCategoryDTO>> = MutableLiveData()
    val listDisplayKind: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listKind: MutableLiveData<MutableList<KindDTO>> = MutableLiveData()
    val listImages: MutableLiveData<ArrayList<LocalImage>> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData("")
    val price: MutableLiveData<String> = MutableLiveData("")
    val quantity: MutableLiveData<String> = MutableLiveData("")
    val description: MutableLiveData<String> = MutableLiveData("")
    val kind: MutableLiveData<String> = MutableLiveData("")
    val category: MutableLiveData<String> = MutableLiveData("")
    private val listOldImageNames: MutableList<String> = mutableListOf()
    var token: TokenDTO? = null

    init {
        listImages.value = arrayListOf()
        listDisplayCategories.value = mutableListOf()
        listCategories.value = mutableListOf()
        listDisplayKind.value = mutableListOf()
        listKind.value = mutableListOf()
        loadAllCategories()
        loadAllKind()
    }

    fun getInfo(product: ProductDetail, images: Array<Image>) {
        selectedProduct = product
        if (selectedProduct != null) {
            name.value = selectedProduct!!.name
            price.value = selectedProduct!!.price.toString()
            quantity.value = selectedProduct!!.availableQuantities.toString()
            description.value = selectedProduct!!.description
        }
        val listImagesTempt = arrayListOf<LocalImage>()
        for (image in images) {
            image.imageUri?.let { listImagesTempt.add(LocalImage(it, "")) }
            val path = "https://firebasestorage.googleapis.com/v0/b/kltn-admin-5643f.appspot.com/o/"
            val endOfName = image.imgURL.indexOf("?alt=media")
            listOldImageNames.add(image.imgURL.substring(path.length, endOfName))
        }
        listImages.value = listImagesTempt
    }

    private fun loadLocalKind() {
        for (kindDTO in listKind.value!!) {
            if (kindDTO.id == selectedProduct!!.kindId) {
                kind.value = "${kindDTO.id} - ${kindDTO.name}"
                break
            }
        }
    }

    private fun loadLocalCategory() {
        for (kindDTO in listKind.value!!) {
            if (kindDTO.id == selectedProduct!!.kindId) {
                for (categoryItem in listCategories.value!!) {
                    if (categoryItem.id == selectedProduct?.kindId) {
                        category.value = "${categoryItem.id} - ${categoryItem.name}"
                        break
                    }
                }
                break
            }
        }
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
                        withContext(Dispatchers.Main) {
                            loadLocalKind()
                            loadLocalCategory()
                        }
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
                        withContext(Dispatchers.Main) {
                            loadLocalKind()
                            loadLocalCategory()
                        }
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

    var numDeleted: AtomicInteger = AtomicInteger(0)
    fun deleteOldImageFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            listOldImageNames.stream().forEach { imgName ->
                val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child(imgName)
                imageRef.delete().addOnFailureListener { e ->
                    e.printStackTrace()
                    status.postValue(LoadingStatus.Fail)
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        numDeleted.getAndIncrement()
                    }
                }
            }
        }
    }

    private val numLoaded = AtomicInteger(0)
    fun loadImageToFirebase(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            listImages.value!!.stream().forEach { it ->
                if (!it.imageUri.toString()
                        .contains("https://firebasestorage.googleapis.com/v0/b/kltn-admin-5643f.appspot.com/o/")
                ) {
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
                } else {
                    listImageUrl.add(it.imageUri.toString())
                    val path =
                        "https://firebasestorage.googleapis.com/v0/b/kltn-admin-5643f.appspot.com/o/"
                    val endOfName = it.imageUri.toString().indexOf("?alt=media")
                    val remainImageName = it.imageUri.toString().substring(path.length, endOfName)
                    numLoaded.incrementAndGet()
                    listOldImageNames.remove(remainImageName)
                    if (numLoaded.get() == listImages.value!!.size) {
                        requestSaveImageUrl(productId)
                    }
                }
            }
        }
    }


    private val listImageUrl = mutableListOf<String>()
    fun requestSaveImageUrl(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listProductImageDTO = mutableListOf<NewProductImageDTO>()
                var index = 1
                listImageUrl.stream().forEach {
                    listProductImageDTO.add(createNewProductImageDTO(productId, it, index))
                    index += 1
                }
                val response = productRepository.updateProductImages(
                    productId,
                    listProductImageDTO,
                    "${token?.tokenType.toString()} ${token?.accessToken.toString()}"
                )
                if (response.isSuccessful) {
                    status.postValue(LoadingStatus.Success)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            requestSaveImageUrl(productId)
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
        }
    }

    val status: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun requestCreateNewProduct() {
        status.postValue(LoadingStatus.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newProductDTO = createNewProductDTO()
                val responseProduct = productRepository.updateProduct(
                    selectedProduct!!.id,
                    newProductDTO,
                    "${token?.tokenType.toString()} ${token?.accessToken.toString()}"
                )
                if (responseProduct.isSuccessful) {
                    val newProductId = responseProduct.body()
                    if (newProductId != null) {
                        loadImageToFirebase(selectedProduct!!.id)
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

    private fun toNewProductImageDTO(
        imageUrl: String,
        index: Int,
        productId: Long
    ): NewProductImageDTO {
        return NewProductImageDTO(
            imageUrl,
            index,
            productId
        )
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
        listImages.value = listTempt
    }
}