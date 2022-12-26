package com.example.aposs_admin.ui_controller.detail_predict_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.network.PredictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class DetailPredictViewModel @Inject constructor(
    private val predictRepository: PredictRepository
) : ViewModel(){

    val detailPredictDTO: MutableLiveData<PredictionDetailDTO> = MutableLiveData()

    fun loadDetailPredictDTO(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = predictRepository.getDetailPredict(id)
                if (response.isSuccessful) {
                    detailPredictDTO.postValue(response.body())
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadDetailPredictDTO(id)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}