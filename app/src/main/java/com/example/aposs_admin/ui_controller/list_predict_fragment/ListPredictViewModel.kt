package com.example.aposs_admin.ui_controller.list_predict_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.PredictionInfo
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.PredictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ListPredictViewModel @Inject constructor(
    private val predictRepository: PredictRepository,
    private val authRepository: AuthRepository
) : ViewModel() {


    val listAllPredictRecord: MutableLiveData<MutableList<PredictionRecordDTO>> = MutableLiveData(
        mutableListOf()
    )

    init {
        loadAllPredictRecord()
    }

    fun loadAllPredictRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = predictRepository.getAllPredict()
                if (response.isSuccessful) {
                    listAllPredictRecord.postValue(response.body() as MutableList<PredictionRecordDTO>?)
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadAllPredictRecord()
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}