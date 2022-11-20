package com.example.aposs_admin.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.aposs_admin.model.DetailCategory
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.network.CategoryRepository
import com.example.aposs_admin.network.KindRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.nio.file.WatchEvent.Kind
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.streams.toList

@HiltViewModel
class AllCategoryViewModel @Inject constructor(
    private val categoriesRepository: CategoryRepository,
    private val subcategoryRepository: KindRepository
    ) : ViewModel() {

    private val _lstCategory = MutableLiveData<MutableList<DetailCategory>>()
    val allCategoriesName = MutableLiveData<MutableList<String>>()
    val currentCategory = MutableLiveData<DetailCategory>()
    
    var currentSubcategories: List<KindDTO>?= mutableListOf()
    val currentSubcategoryNames = MutableLiveData<MutableList<String>>()

    val subcategoryLoadingStatus = MutableLiveData<LoadingStatus>()
    
    val status = MutableLiveData<LoadingStatus>()
    val totalCategoryImage = MutableLiveData<String>()
    val totalSubcategory = MutableLiveData<String>()

    init {
        loadAllCategories()
    }

    private fun loadingALlSubCategories(){
        currentCategory.value.let {
            if(it != null){
                currentSubcategoryNames.value = mutableListOf()
                subcategoryLoadingStatus.value = LoadingStatus.Loading
                viewModelScope.launch(Dispatchers.IO){
                    try {
                        val allSubcategoriesResponse = subcategoryRepository.getAllSubCategoryByCategoryId(it.id)
                        if(allSubcategoriesResponse.isSuccessful){
                            currentSubcategories = allSubcategoriesResponse.body()
                            if (currentSubcategories != null){
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
        _lstCategory.value.let {
            if (it != null){
                if (position < it.count())
                currentCategory.value = it[position]
                totalCategoryImage.postValue(it[position].images.size.toString())
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
                    _lstCategory.postValue(listCategory)
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