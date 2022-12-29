package com.example.aposs_admin.ui_controller.detail_predict_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.model.dto.SaleDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.PredictRepository
import com.example.aposs_admin.network.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class DetailPredictViewModel @Inject constructor(
    private val predictRepository: PredictRepository,
    private val saleRepository: SaleRepository,
    private val authRepository: AuthRepository
) : ViewModel(){

    val detailPredictDTO: MutableLiveData<PredictionDetailDTO> = MutableLiveData(PredictionDetailDTO())
    val listOldSale: MutableLiveData<MutableList<SaleDTO>> = MutableLiveData(mutableListOf())

    fun loadDetailPredictDTO(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = predictRepository.getDetailPredict(id)
                if (response.isSuccessful) {
                    detailPredictDTO.postValue(response.body())
                    response.body()?.let { loadSales(it) }
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

    fun loadSales(predictionDetailDTO: PredictionDetailDTO) {
//       if (predictionDetailDTO.productID == -1L) {
//           loadSaleByProduct(predictionDetailDTO.productID)
//           return
//       }
        loadSaleByKind(predictionDetailDTO.subcategoryId)
        Log.i("DetailPredictFragment", "loadoldsale")
    }

    private fun loadSaleByKind(kindID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = saleRepository.loadSaleDataBySubcategory(kindID)
                if (response.isSuccessful) {
                    listOldSale.postValue(response.body() as MutableList<SaleDTO>?)
                }
                Log.i("DetailPredictFragment", response.code().toString())
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadSaleByKind(kindID)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadSaleByProduct(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = saleRepository.loadSaleDataByProduct(productId)
                if (response.isSuccessful) {
                    listOldSale.postValue(response.body() as MutableList<SaleDTO>?)
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadSaleByProduct(productId)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}