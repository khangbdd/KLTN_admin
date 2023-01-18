package com.example.aposs_admin.ui_controller.create_predict_fragment

import android.util.Log
import com.example.aposs_admin.model.dto.PredictionInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.PredictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class CreatePredictViewModel @Inject constructor(
    private val predictRepository: PredictRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    var selectedKindId: MutableLiveData<Long>  = MutableLiveData(-1)
    var selectedProductId: MutableLiveData<Long> = MutableLiveData(-1)
    var selectedKindName: MutableLiveData<String>  = MutableLiveData("Chọn danh mục dự báo")
    var selectedProductName: MutableLiveData<String> = MutableLiveData("Chọn sản phẩm dự báo")
    var numberOfDate: MutableLiveData<String> =  MutableLiveData()
    var limitNumberOfMonth: MutableLiveData<Int> = MutableLiveData(0)
    var limitNumberOfDate: MutableLiveData<Int> = MutableLiveData(0)

    private fun reset() {
        selectedKindId.value = -1
        selectedProductId.value = -1
        selectedKindName.value = "Chọn danh mục dự báo"
        selectedProductName.value = "Chọn sản phẩm dự báo"
        numberOfDate.value =  ""
        limitNumberOfMonth.value = 0
        limitNumberOfDate.value = 0
    }


    private fun refreshLimitMonth() {
        if (selectedKindId.value!! != -1L) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    var productId: Long? = null
                    if (selectedProductId.value != -1L) {
                        productId = selectedProductId.value
                    }
                    val response =
                        predictRepository.getLimit("M", selectedKindId.value!!, productId)
                    if (response.isSuccessful) {
                        limitNumberOfMonth.postValue(response.body())
                    }
                } catch (e: Exception) {
                    if (e is SocketTimeoutException) {
                        refreshLimitMonth()
                    } else {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun refreshLimitDate() {
        if (selectedKindId.value!! != -1L) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    var productId: Long? = null
                    if (selectedProductId.value != -1L) {
                        productId = selectedProductId.value
                    }
                    val response =
                        predictRepository.getLimit("D", selectedKindId.value!!, productId)
                    if (response.isSuccessful) {
                        limitNumberOfDate.postValue(response.body())
                    }
                } catch (e: Exception) {
                    if (e is SocketTimeoutException) {
                        refreshLimitDate()
                    } else {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun refreshLimit(frequencyFull: String) {
        if (frequencyFull == "Tháng") {
            refreshLimitMonth()
        } else {
            refreshLimitDate()
        }
    }

    fun createPredict(frequencyFull: String, successToasting: ()->Unit) {
        var frequency = "M"
        if (frequencyFull == "Ngày") {
            frequency = "D"
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = predictRepository.createPredict(
                    PredictionInfo(frequency, selectedProductId.value!!, selectedKindId.value!!, numberOfDate.value!!.toInt()),
                    authRepository.getCurrentAccessTokenFromRoom()
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        successToasting()
                        reset()
                    }
                }else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            createPredict(frequencyFull, successToasting)
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
                    }
                }
            } catch (e:Exception) {
                if (e is SocketTimeoutException) {
                    createPredict(frequencyFull, successToasting)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

}