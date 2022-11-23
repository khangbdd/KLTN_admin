package com.example.aposs_admin.ui_controller.account_manage_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.network.AccountRepository
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class AccountManageViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    val listAccount : MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())

    init {
        loadAccount()
    }

    private val loadStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    private fun loadAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                val response = accountRepository.getAllAccount(accessToken.toString())
                if (response.isSuccessful) {
                    listAccount.postValue(response.body() as MutableList<String>?)
                    loadStatus.postValue(LoadingStatus.Success)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            loadAccount()
                        } else {
                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        loadStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    loadAccount()
                } else {
                    loadStatus.postValue(LoadingStatus.Fail)
                    Log.e("Exception", e.toString())
                }
            }
        }
    }

    private val deleteStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun deleteAccount(account: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                val response = accountRepository.deleteAccount(account, accessToken.toString())
                if (response.isSuccessful) {
                    deleteStatus.postValue(LoadingStatus.Success)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            deleteAccount(account)
                        } else {
                            deleteStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        deleteStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    deleteAccount(account)
                } else {
                    deleteStatus.postValue(LoadingStatus.Fail)
                    Log.e("Exception", e.toString())
                }
            }
        }
    }
}