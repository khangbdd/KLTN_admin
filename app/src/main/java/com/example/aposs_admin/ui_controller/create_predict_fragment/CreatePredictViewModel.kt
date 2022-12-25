package com.example.aposs_admin.ui_controller.create_predict_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePredictViewModel @Inject constructor(): ViewModel() {

    val selectedKindId: Long  = 0L
    val selectedProductId: Long = 0L
    val numberOfDate: MutableLiveData<Int> =  MutableLiveData()
    val limitNumberOfDate: MutableLiveData<Int> = MutableLiveData()


}