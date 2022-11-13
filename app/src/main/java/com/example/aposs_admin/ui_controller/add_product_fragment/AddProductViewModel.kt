package com.example.aposs_admin.ui_controller.add_product_fragment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.network.CategoryRepository
import com.example.aposs_admin.network.KindRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val kindRepository: KindRepository,
) : ViewModel() {

    val listDisplayCategories: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listCategories: MutableLiveData<MutableList<DetailCategoryDTO>> = MutableLiveData()
    val listDisplayKind: MutableLiveData<MutableList<String>> = MutableLiveData()
    val listKind: MutableLiveData<MutableList<KindDTO>> = MutableLiveData()
    val listImages: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val listImagesPath: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData()
    val price: MutableLiveData<String> = MutableLiveData()
    val quantity: MutableLiveData<String> = MutableLiveData()
    val description: MutableLiveData<String> = MutableLiveData()

    init {
        listImages.value = arrayListOf()
        listImagesPath.value = arrayListOf()
        listDisplayCategories.value = mutableListOf()
        listCategories.value = mutableListOf()
        listDisplayKind.value = mutableListOf()
        listKind.value = mutableListOf()
        loadAllCategories()
        loadAllKind()
    }

    fun addImages(uri: Uri) {
        val listTempt = listImages.value
        listTempt!!.add(uri.toString())
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
            Log.i("TTTTTTTT", "whyyyyyyyyyyyyyyyyy1")
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
            listImagesPath.value!!.stream().forEach { it ->
                val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                Log.i("TTTTTTTTTTTTT", it)
                val imageRef = storageRef.child("${name.value}-${index}.jpeg")
                val file = Uri.fromFile(File(it.toString()))
                val uploadTask = imageRef.putFile(file)
                uploadTask.addOnFailureListener { e ->
                    e.printStackTrace()
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.i("TTTTTTTTTTTTT", downloadUri.toString())
                    }
                }
                index++
            }
        }
    }
}