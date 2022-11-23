package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.OrderDTO
import com.example.aposs_admin.network.service.OrderService
import com.example.aposs_admin.util.OrderStatus
import retrofit2.Response
import javax.inject.Inject

class OrderRepository @Inject constructor() {
    private val orderService: OrderService by lazy {
        RetrofitInstance.retrofit.create(OrderService::class.java)
    }

    suspend fun getAllOrderWithStatus(
        orderStatus: OrderStatus?,
        accessToken: String?
    ): Response<List<OrderDTO>> {
        return orderService.getAllOrderWithStatus(orderStatus, accessToken)
    }

    suspend fun setOrderStatus(
        id: Long?,
        orderStatus: OrderStatus?,
        accessToken: String?
    ): Response<String> {
        return orderService.setOrderStatus(id, orderStatus, accessToken)
    }

    suspend fun confirmCompletedPayment(orderId: Long, accessToken: String?) : Response<String> {
        return orderService.confirmCompletedPayment(orderId, accessToken)
    }
}