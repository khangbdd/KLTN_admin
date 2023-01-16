package com.example.aposs_admin.ui_controller.detail_predict_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.SaleDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.PredictRepository
import com.example.aposs_admin.network.SaleRepository
import com.example.aposs_admin.util.PredictionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DetailPredictViewModel @Inject constructor(
    private val predictRepository: PredictRepository,
    private val saleRepository: SaleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val detailPredictDTO: MutableLiveData<PredictionDetailDTO> =
        MutableLiveData(PredictionDetailDTO())
    val listOldSale: MutableLiveData<MutableList<SaleDTO>> = MutableLiveData(mutableListOf())
    val listFullDate: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
//    var startIndex: MutableLiveData<Int> = MutableLiveData(-1)

    fun loadDetailPredictDTO(id: Long, onSuccess: (PredictionStatus) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = predictRepository.getDetailPredict(id)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        detailPredictDTO.value = (response.body())
                        response.body()?.let { loadSales(it) }
                        response.body()?.predictionStatus?.let {
                            onSuccess(it)
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadDetailPredictDTO(id, onSuccess)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    fun loadFullDate(prediction: PredictionDetailDTO, real: List<SaleDTO>) {
        val temptList = mutableListOf<String>()
        real.stream().forEach {
            temptList.add(it.date!!)
        }
        prediction.recordItemDTOList?.stream()?.forEach {
            if (!temptList.contains(it.date!!)) {
                temptList.add(it.date)
            }
        }
        temptList.sortBy {
            toMillis(it)
        }
        listFullDate.postValue(temptList)
        loadListOfIndex(real, prediction, temptList)
    }

//    fun compare(firstDateString: String, secondDateString: String): Boolean {
//        val sdf = SimpleDateFormat("dd-MM-yyyy")
//        val firstDateMillis = sdf.parse(firstDateString)?.time?:0
//        val secondDateMillis = sdf.parse(secondDateString)?.time?:0
//        return firstDateMillis <= secondDateMillis
//    }

    fun toMillis(dateString: String): Long {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.parse(dateString)?.time?:0
    }

    fun loadSales(predictionDetailDTO: PredictionDetailDTO) {
        if (predictionDetailDTO.productID != -1L) {
            loadSaleByProduct(
                predictionDetailDTO.productID,
                predictionDetailDTO.frequency,
                predictionDetailDTO
            )
            return
        }
        loadSaleByKind(
            predictionDetailDTO.subcategoryId,
            predictionDetailDTO.frequency,
            predictionDetailDTO
        )
    }

    private fun loadSaleByKind(
        kindID: Long,
        frequency: String,
        predictionDetailDTO: PredictionDetailDTO
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = saleRepository.loadSaleDataBySubcategory(kindID, frequency)
                if (response.isSuccessful) {
                    response.body()?.let { loadFullDate(predictionDetailDTO, it) }
                    withContext(Dispatchers.Main) {
                        listOldSale.value = (response.body() as MutableList<SaleDTO>?)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadSaleByKind(kindID, frequency, predictionDetailDTO)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadSaleByProduct(
        productId: Long,
        frequency: String,
        predictionDetailDTO: PredictionDetailDTO
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = saleRepository.loadSaleDataByProduct(productId, frequency)
                if (response.isSuccessful) {
                    response.body()?.let { loadFullDate(predictionDetailDTO, it) }
                    withContext(Dispatchers.Main) {
                        listOldSale.value = (response.body() as MutableList<SaleDTO>?)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadSaleByProduct(productId, frequency, predictionDetailDTO)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    val listIndexOfPredict: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
    val listIndexOfSale: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())
//    fun getStartIndex(predictionDetailDTO: PredictionDetailDTO, fullDate: List<String>) {
//        val startPredictDate = predictionDetailDTO.recordItemDTOList!![0].date
//        var startTemptIndex = 0
//        for (date in fullDate) {
//            Log.i("DetailPredictFragment2", date)
//        }
//        for (date in fullDate) {
//            if (startPredictDate == date) {
//                startIndex.postValue(startTemptIndex)
//                return
//            }
//            startTemptIndex++
//        }
//    }

    fun loadListOfIndex(real: List<SaleDTO>, predictionDetailDTO: PredictionDetailDTO, fullDate: List<String>) {
        val tempIndexesOfPredict = mutableListOf<Int>()
        val tempIndexesOfSale = mutableListOf<Int>()
        predictionDetailDTO.recordItemDTOList?.stream()?.forEach {
            tempIndexesOfPredict.add(fullDate.indexOf(it.date))
        }
        real.stream().forEach {
            tempIndexesOfSale.add(fullDate.indexOf(it.date))
        }
        listIndexOfPredict.postValue(tempIndexesOfPredict)
        listIndexOfSale.postValue(tempIndexesOfSale)
    }
}