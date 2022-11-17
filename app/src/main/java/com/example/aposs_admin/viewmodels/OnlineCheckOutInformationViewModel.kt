package com.example.aposs_admin.viewmodels


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.BankingInformationDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.BankingInformationRepository
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.util.StringValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class OnlineCheckOutInformationViewModel  @Inject constructor(
    private val bankingInformationRepository: BankingInformationRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    val bankingInformationDTO: MutableLiveData<BankingInformationDTO> = MutableLiveData()
    var currentBankingInformation: BankingInformationDTO? = null
    val status = MutableLiveData<LoadingStatus>()
    val updateStatus = MutableLiveData<LoadingStatus>()
    val isChangeable = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    init {
        loadBankingInformation()
    }



    private fun checkValidInformation(): Boolean{
        bankingInformationDTO.value.let {
            if (it != null){
                if(it.receiverName.isBlank() || it.bankName.isBlank() || it.branch.isBlank() || it.accountNumber.isBlank()){
                    errorMessage.value = "Vui lòng điền đầy đủ thông tin!"
                    return false
                }
                if (StringValidator.isStringContainNumberOrSpecialCharacter(it.receiverName)){
                    errorMessage.value = "Tên người nhận không thể có số hoặc kí tự đặc biệt!"
                    return false
                }
                if(StringValidator.isStringContainSpecialCharacter(it.bankName)){
                    errorMessage.value = "Tên ngân hàng không thể có kí tự đặc biệt!"
                    return false
                }
                if(!StringValidator.isNumberOnly(it.accountNumber)){
                    errorMessage.value = "Số tài khoản không thể có chữ hoặc kí tự đặc biệt!"
                    return false
                }
            }
        }
        errorMessage.value = null
        return true
    }

    fun checkChangeable(){
        bankingInformationDTO.value.let {
            it?.equalTo(currentBankingInformation).let { isEqual ->
                if (isEqual!= null){
                    isChangeable.value = !isEqual
                }
            }
        }

    }

    fun changeBankingInformation(){
        if(checkValidInformation()){
            updateStatus.value = LoadingStatus.Loading
            runBlocking {
                viewModelScope.launch {
                    val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                    if (!accessToken.isNullOrBlank()) {
                        try {
                            val response =
                                bankingInformationRepository.updateBankingInformation(accessToken, bankingInformationDTO.value!!)
                            if (response.isSuccessful) {
                                updateStatus.value = LoadingStatus.Success
                            } else {
                                if (response.code() == 401) {
                                    if (authRepository.loadNewAccessTokenSuccess()) {
                                       changeBankingInformation()
                                    } else {
                                        updateStatus.value = LoadingStatus.Fail
                                    }
                                } else {
                                    updateStatus.value = LoadingStatus.Fail
                                }
                            }
                        }catch (e: Exception){
                            if (e is SocketTimeoutException) {
                               changeBankingInformation()
                            } else {
                                updateStatus.value = LoadingStatus.Fail
                                Log.e("Exception", e.toString())
                            }
                        }
                    } else {
                        updateStatus.value = LoadingStatus.Fail
                    }
                }
            }
        }
    }


    private fun loadBankingInformation() {
        status.postValue(LoadingStatus.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    bankingInformationRepository.getBankingInformationData()
                if (response.isSuccessful) {
                    bankingInformationDTO.postValue(response.body())
                    currentBankingInformation = response.body()?.copy()
                    status.postValue(LoadingStatus.Success)
                } else {
                    status.postValue(LoadingStatus.Fail)
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadBankingInformation()
                } else {
                    status.postValue(LoadingStatus.Fail)
                    Log.e("Exception", e.toString())
                }
            }
        }
    }
}