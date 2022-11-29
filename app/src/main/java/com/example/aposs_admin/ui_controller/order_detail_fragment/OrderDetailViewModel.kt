package com.example.aposs_admin.ui_controller.order_detail_fragment

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.model.Order
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.OrderRepository
import com.example.aposs_admin.util.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    var token: TokenDTO? = null

    var detailOrder: MutableLiveData<Order> = MutableLiveData<Order>()

    @SuppressLint("CheckResult")
    fun setOrderStatus(id: Long, orderStatus: OrderStatus?, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderRepository.setOrderStatus(
                    id,
                    orderStatus,
                    authRepository.getCurrentAccessTokenFromRoom()
                )
                if (response.isSuccessful) {
                    val newStatusOrder: Order? = detailOrder.value
                    if (orderStatus != null) {
                        newStatusOrder?.status = orderStatus
                        detailOrder.value!!.status = orderStatus
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                }else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            setOrderStatus(id, orderStatus, onSuccess)
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    setOrderStatus(id, orderStatus, onSuccess)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }
}