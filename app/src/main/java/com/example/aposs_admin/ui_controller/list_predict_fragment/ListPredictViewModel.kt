package com.example.aposs_admin.ui_controller.list_predict_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.DetailCategoryDTO
import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.PredictionInfo
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.PredictRepository
import com.example.aposs_admin.util.LoadingStatus
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

    fun deletePredictRecord(predictionRecordDTO: PredictionDetailDTO, onSuccess: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = predictRepository.deletePredict(predictionRecordDTO.id, authRepository.getCurrentAccessTokenFromRoom())
                if (response.isSuccessful) {
                    loadAllPredictRecord()
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            deletePredictRecord(predictionRecordDTO, onSuccess)
                        } else {
                            // fail
                        }
                    } else {
                        // fail
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    deletePredictRecord(predictionRecordDTO, onSuccess)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}