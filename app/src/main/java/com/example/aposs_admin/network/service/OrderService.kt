package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.OrderDTO
import com.example.aposs_admin.util.OrderStatus
import retrofit2.Response
import retrofit2.http.*


interface OrderService {
    @GET("order/order-by-status")
    suspend fun getAllOrderWithStatus(
        @Query("status") status: OrderStatus?,
        @Header("Authorization") accessToken: String?
    ): Response<List<OrderDTO>>

    @PUT("order/change-order-status/{id}")
    suspend fun setOrderStatus(
        @Path("id") id: Long?,
        @Query("status") orderStatus: OrderStatus?,
        @Header("Authorization") accessToken: String?
    ): Response<String>

    @PUT("order/confirm-completed-payment/{id}")
    suspend fun confirmCompletedPayment(
        @Path(value = "id") orderId: Long,
        @Header("Authorization") accessToken: String?
    ): Response<String>
}