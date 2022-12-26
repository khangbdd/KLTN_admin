package com.example.aposs_admin.ui_controller.list_kind_fragment

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.HomeProduct
import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.network.KindRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class ListKindViewModel @Inject constructor(
    private val kindRepository: KindRepository
): ViewModel() {
    val searchText: MutableLiveData<String> = MutableLiveData("")
    val listDisplay: MutableLiveData<MutableList<KindDTO>> = MutableLiveData(
        mutableListOf()
    )
    private val listOfAllKind: MutableLiveData<MutableList<KindDTO>> = MutableLiveData(
        mutableListOf()
    )

    init {
        loadAllKind()
    }

    private val loadingStatus: MutableLiveData<LoadingStatus> = MutableLiveData(LoadingStatus.Loading)
    private fun loadAllKind() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = kindRepository.getAllKind()
                if (response.isSuccessful) {
                    listOfAllKind.postValue(response.body() as MutableList<KindDTO>?)
                    (response.body() as MutableList<KindDTO>?)?.let { filter(it) }
                }
            } catch (e:Exception) {
                if (e is SocketTimeoutException) {
                    loadAllKind()
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun filter(listFull: MutableList<KindDTO>) {
        listDisplay.postValue(listFull.stream().filter {
            if (searchText.value != "" || searchText.value != null) {
                it.name.contains(searchText.value!!)
            } else {
                true
            }
        }.collect(Collectors.toList()))
    }

    fun setUpObserver(owner: LifecycleOwner) {
        searchText.observe(owner) {
            filter(listOfAllKind.value!!)
        }
    }
}