package com.example.aposs_admin.ui_controller.order_fragment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.OrderDTO
import com.example.aposs_admin.model.dto.OrderItemDTO
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.Order
import com.example.aposs_admin.model.OrderBillingItem
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.OrderRepository
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.util.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.stream.Collectors
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
    ) : ViewModel() {
    var token: TokenDTO? = null
    var lstDisplay: MutableLiveData<ArrayList<Order>> = MutableLiveData<ArrayList<Order>>()
    fun loadToken(token: TokenDTO?) {
        this.token = token
        loadOrder(OrderStatus.Pending)
    }

    private var lstOrder: MutableLiveData<ArrayList<OrderDTO>> = MutableLiveData<ArrayList<OrderDTO>>()

    @SuppressLint("CheckResult")
    fun loadOrder(orderStatus: OrderStatus?) {
        lstDisplay.postValue(arrayListOf())
        lstOrder.postValue(arrayListOf())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderRepository.getAllOrderWithStatus(
                    orderStatus,
                    authRepository.getCurrentAccessTokenFromRoom()
                )
                if (response.isSuccessful) {
                    val orderDTOs = response.body()
                    val listAttempt: ArrayList<Order> = arrayListOf()
                    val listAttemptDTO: ArrayList<OrderDTO> = arrayListOf()
                    orderDTOs?.stream()?.forEach { orderDTO: OrderDTO ->
                        listAttempt.add(
                            convertToOrder(orderDTO)
                        )
                        listAttemptDTO.add(orderDTO)
                    }
                    lstOrder.postValue(listAttemptDTO)
                    listAttempt.stream().forEach {
                        Log.i("TTTTTTTTT", it.isOnlinePayment.toString())
                    }
                    lstDisplay.postValue(listAttempt)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            loadOrder(orderStatus)
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    loadOrder(orderStatus)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    fun confirmCompletedPayment(orderId: Long, choseOrderStatus: OrderStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderRepository.confirmCompletedPayment(orderId, authRepository.getCurrentAccessTokenFromRoom())
                if (response.isSuccessful) {
                    loadOrder(choseOrderStatus)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            confirmCompletedPayment(orderId, choseOrderStatus)
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
                    }
                }
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    confirmCompletedPayment(orderId, choseOrderStatus)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun setOrderStatus(id: Long, orderStatus: OrderStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = orderRepository.setOrderStatus(
                    id,
                    orderStatus,
                    authRepository.getCurrentAccessTokenFromRoom()
                )
                if (response.isSuccessful) {
                    if (orderStatus === OrderStatus.Confirmed) {
                        loadOrder(OrderStatus.Pending)
                    }
                    if (orderStatus === OrderStatus.Delivering) {
                        loadOrder(OrderStatus.Confirmed)
                    }
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            setOrderStatus(id, orderStatus)
                        } else {
//                            loadStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
//                        loadStatus.postValue(LoadingStatus.Fail)
                    }
                }
            } catch (e:Exception) {
                if (e is SocketTimeoutException) {
                    setOrderStatus(id, orderStatus)
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun convertToOrder(orderDTO: OrderDTO): Order {
        return Order(
            orderDTO.id,
            orderDTO.orderTime,
            orderDTO.orderStatus,
            orderDTO.address,
            orderDTO.paymentStatus,
            orderDTO.isOnlinePayment,
            ArrayList(orderDTO.orderItemDTOList.stream().map { orderItemDTO ->
                convertToOrderItem(
                    orderItemDTO
                )
            }.collect(Collectors.toList())),
            orderDTO.totalPrice
        )
    }

    private fun convertToOrderItem(orderItemDTO: OrderItemDTO): OrderBillingItem {
        return OrderBillingItem(
            orderItemDTO.id,
            orderItemDTO.cartId,
            orderItemDTO.setId,
            Image(orderItemDTO.imageUrl),
            orderItemDTO.name,
            orderItemDTO.price,
            orderItemDTO.quantity,
            orderItemDTO.property
        )
    }

    init {
        lstDisplay.value = arrayListOf()
    }
}