package com.example.aposs_admin.ui_controller.change_predict_info_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.UpdatePredictDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.PredictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ChangePredictInfoViewModel @Inject constructor(
    private val predictRepository: PredictRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    var id: Long = 0L
    val nameLiveData: MutableLiveData<String> = MutableLiveData("")
    val descriptionLiveData: MutableLiveData<String> = MutableLiveData("")

    fun setCurrentIdAndNameAndDescription(predictId: Long, name: String, description: String) {
        nameLiveData.value = name
        descriptionLiveData.value = description
        id = predictId
    }

    fun updatePredict(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (id != 0L) {
                    val updatePredictDTO =
                        UpdatePredictDTO(id, nameLiveData.value!!, descriptionLiveData.value!!)
                    val response = predictRepository.updatePredict(
                        updatePredictDTO,
                        authRepository.getCurrentAccessTokenFromRoom()
                    )
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    } else {
                        Log.i("Change Predict", response.code().toString())
                        if (response.code() == 401) {
                            if (authRepository.loadNewAccessTokenSuccess()) {
                                updatePredict(onSuccess)
                            } else {
                                // fail
                            }
                        } else {
                            // fail
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    updatePredict(onSuccess)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}