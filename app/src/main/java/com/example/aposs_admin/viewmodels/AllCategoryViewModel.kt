package com.example.aposs_admin.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.aposs_admin.model.DetailCategory
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.Subcategory
import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.model.dto.NewSubcategoryDTO
import com.example.aposs_admin.model.dto.ProductDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.CategoryRepository
import com.example.aposs_admin.network.KindRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class AllCategoryViewModel @Inject constructor(
    private val categoriesRepository: CategoryRepository,
    private val subcategoryRepository: KindRepository,
    private val authRepository: AuthRepository
    ) : ViewModel() {

    // all category
    val allCategories = MutableLiveData<MutableList<DetailCategory>>()
    val allCategoriesName = MutableLiveData<MutableList<String>>()
    val status = MutableLiveData<LoadingStatus>()
    // detail category
    val currentCategory = MutableLiveData<DetailCategory>()
    var currentSubcategories: List<Subcategory>?= mutableListOf()
    val currentSubcategoryNames = MutableLiveData<MutableList<String>>()
    val subcategoryLoadingStatus = MutableLiveData<LoadingStatus>()
    val totalCategoryImage = MutableLiveData<String>()
    val totalSubcategory = MutableLiveData<String>()
    // detail subcategory
    val currentSubcategory = MutableLiveData<Subcategory>()
    val totalSubcategoryProduct = MutableLiveData<String>()
    val updateSubcategoryStatus = MutableLiveData<LoadingStatus>()


    init {
        loadAllCategories()
    }
    
    fun setCurrentSubcategory(position: Int){
        currentSubcategories.let {
            if(it != null){
                if (position < it.size){
                    updateSubcategoryStatus.value = LoadingStatus.Init
                    currentSubcategory.value = it[position]
                    totalSubcategoryProduct.value = it[position].Products.size.toString()
                }
            }
        }
    }

    fun updateSubcategory(id: Long, newSubcategoryDTO: NewSubcategoryDTO, newCategoryName: String){
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                if (!accessToken.isNullOrBlank()) {
                    try {
                        val response = subcategoryRepository.updateSubcategory(accessToken, id, newSubcategoryDTO)
                        if (response.isSuccessful) {
                            currentSubcategory.value.let {
                                if (it != null){
                                    it.name = newSubcategoryDTO.name
                                    it.categoryName = newCategoryName
                                    it.image = Image(newSubcategoryDTO.image)
                                }
                            }
                            updateSubcategoryStatus.postValue(LoadingStatus.Success)
                        } else {
                            if (response.code() == 401) {
                                if (authRepository.loadNewAccessTokenSuccess()) {
                                    updateSubcategory(id, newSubcategoryDTO, newCategoryName)
                                } else {
                                    updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                                }
                            } else {
                                updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                            }
                        }
                    }catch (e: Exception){
                        if (e is SocketTimeoutException) {
                            updateSubcategory(id, newSubcategoryDTO, newCategoryName)
                        } else {
                            updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                            Log.e("Exception", e.toString())
                        }
                    }
                }else{
                    updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                }
            }
        }
    }

    fun deleteSubcategory(id: Long){
        updateSubcategoryStatus.postValue(LoadingStatus.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                if (!accessToken.isNullOrBlank()) {
                    try {
                        val response = subcategoryRepository.deleteSubcategory(accessToken, id)
                        if (response.isSuccessful) {
                            updateSubcategoryStatus.postValue(LoadingStatus.Success)
                        } else {
                            if (response.code() == 401) {
                                if (authRepository.loadNewAccessTokenSuccess()) {
                                    deleteSubcategory(id)
                                } else {
                                    updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                                }
                            } else {
                                updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                            }
                        }
                    }catch (e: Exception){
                        if (e is SocketTimeoutException) {
                            deleteSubcategory(id)
                        } else {
                            updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                            Log.e("Exception", e.toString())
                        }
                    }
                }else{
                    updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                }
            }
        }
    }

    fun addNewSubCategory(subcategory: NewSubcategoryDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            val accessToken = authRepository.getCurrentAccessTokenFromRoom()
            if (!accessToken.isNullOrBlank()) {
                try {
                    val response = subcategoryRepository.createSubcategory(
                        accessToken, subcategory
                    )
                    if (response.isSuccessful) {
                        updateSubcategoryStatus.postValue(LoadingStatus.Success)
                    } else {
                        if (response.code() == 401) {
                            if (authRepository.loadNewAccessTokenSuccess()) {
                                addNewSubCategory(subcategory)
                            } else {
                                updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                            }
                        } else {
                            updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                        }
                    }
                } catch (e: Exception) {
                    if (e is SocketTimeoutException) {
                        addNewSubCategory(subcategory)
                    } else {
                        updateSubcategoryStatus.postValue(LoadingStatus.Fail)
                        Log.e("Exception", e.toString())
                    }
                }
            } else {
                updateSubcategoryStatus.postValue(LoadingStatus.Fail)
            }
        }
    }

    fun loadingALlSubCategories(){
        totalSubcategory.postValue(null)
        currentCategory.value.let {
            if(it != null){
                currentSubcategoryNames.value = mutableListOf()
                subcategoryLoadingStatus.value = LoadingStatus.Loading
                viewModelScope.launch(Dispatchers.IO){
                    try {
                        val allSubcategoriesResponse = subcategoryRepository.getAllSubCategoryByCategoryId(it.id)
                        if(allSubcategoriesResponse.isSuccessful){
                            if (allSubcategoriesResponse.body() != null){
                                currentSubcategories = allSubcategoriesResponse.body()!!.map { kindDTO ->
                                    convertToSubcategory(kindDTO)
                                }
                                currentSubcategoryNames.postValue(currentSubcategories!!.stream().map { kindDTO ->
                                    kindDTO.name
                                }.collect(Collectors.toList()))
                                totalSubcategory.postValue(currentSubcategories!!.size.toString())
                            }

                            subcategoryLoadingStatus.postValue(LoadingStatus.Success)
                        }else{
                            subcategoryLoadingStatus.postValue(LoadingStatus.Fail)
                        }
                    }catch (e: Exception){
                        if (e is SocketTimeoutException){
                            loadingALlSubCategories()
                        }else{
                            Log.e("AllCategoryViewModel", e.toString())
                            subcategoryLoadingStatus.postValue(LoadingStatus.Fail)
                        }
                    }
                }
            }
        }
    }

    fun setCurrentCategory(position: Int){
        allCategories.value.let {
            if (it != null){
                if (position < it.count())
                currentCategory.value = it[position]
                val imageCount = it[position].images.size.toString() +"/5"
                totalCategoryImage.postValue(imageCount)
                loadingALlSubCategories()
            }
        }

    }

    private fun loadAllCategories() {
        status.value = LoadingStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allCategoriesResponse = categoriesRepository.getAllCategory()
                if (allCategoriesResponse.isSuccessful) {
                    val listCategory =  allCategoriesResponse.body()?.stream()?.map { detailCategoryDTO ->
                        convertFromDetailCategoryDTOToDetailCategory(detailCategoryDTO)
                    }?.collect(Collectors.toList())
                    allCategories.postValue(listCategory)
                    allCategoriesName.postValue(
                        allCategoriesResponse.body()?.stream()?.map { detailCategoryDTO ->
                            detailCategoryDTO.name
                        }?.collect(Collectors.toList()))
                    status.postValue(LoadingStatus.Success)
                    if (listCategory?.isNotEmpty() == true) {
                        currentCategory.postValue(listCategory[0])
                    }
                } else {
                    status.postValue(LoadingStatus.Fail)
                }

            } catch (e: Exception) {
                if (e is SocketTimeoutException)
                {
                    loadAllCategories()
                } else {
                    Log.e("exception", e.toString())
                    status.postValue(LoadingStatus.Fail)
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

    private fun convertToSubcategory(kindDTO: KindDTO): Subcategory{
        val subcategoriesProduct = kindDTO.products.stream().map {
            convertProductDTOtoHomeProduct(it)
        }.collect(Collectors.toList())

        return Subcategory(kindDTO.id, kindDTO.name, subcategoriesProduct, Image(kindDTO.image), currentCategory.value!!.name)
    }

    private fun convertFromDetailCategoryDTOToDetailCategory(detailCategoryDTO: DetailCategoryDTO): DetailCategory {
        val images: MutableList<Image> = mutableListOf()
        for (item in detailCategoryDTO.images) {
            images.add(Image(item))
        }
        return DetailCategory(
            id = detailCategoryDTO.id,
            name = detailCategoryDTO.name,
            totalProduct = detailCategoryDTO.totalProducts,
            totalPurchase = detailCategoryDTO.totalPurchases,
            rating = detailCategoryDTO.rating,
            images = images
        )
    }

}