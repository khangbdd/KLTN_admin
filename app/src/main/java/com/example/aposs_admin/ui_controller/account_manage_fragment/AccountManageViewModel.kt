package com.example.aposs_admin.ui_controller.account_manage_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.AccountDTO
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.network.AccountRepository
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    e.printStackTrace()
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
                    val listTempt =  listAccount.value
                    listTempt?.remove(account)
                    listAccount.postValue(listTempt)
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
                    e.printStackTrace()
                    Log.e("Exception", e.toString())
                }
            }
        }
    }

    private val changePasswordStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun changePassword(account: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                Log.i("TTTTTTTTTTTTT", account)
                Log.i("TTTTTTTTTTTTT", newPassword)
                val response = accountRepository.changePassword(account, newPassword, accessToken.toString())
                if (response.isSuccessful) {
                    changePasswordStatus.postValue(LoadingStatus.Success)
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            changePassword(account, newPassword, onSuccess)
                        } else {
                            changePasswordStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        changePasswordStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    changePassword(account, newPassword, onSuccess)
                } else {
                    changePasswordStatus.postValue(LoadingStatus.Fail)
                    e.printStackTrace()
                    Log.e("Exception", e.toString())
                }
            }
        }
    }

    private val addingStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun addNewAccount(account: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                val response = accountRepository.addNewAccount(AccountDTO(account, password), accessToken.toString())
                if (response.isSuccessful) {
                    addingStatus.postValue(LoadingStatus.Success)
                    loadAccount()
                    withContext(Dispatchers.IO)
                    {
                        onSuccess()
                    }
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            addNewAccount(account, password, onSuccess)
                        } else {
                            addingStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        addingStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    addNewAccount(account, password, onSuccess)
                } else {
                    addingStatus.postValue(LoadingStatus.Fail)
                    e.printStackTrace()
                    Log.e("Exception", e.toString())
                }
            }
        }
    }
}