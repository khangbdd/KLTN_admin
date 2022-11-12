package com.example.aposs_admin.model.dto;


import com.example.aposs_admin.util.OrderStatus;
import com.example.aposs_admin.util.PaymentStatus
import java.util.Date;

data class OrderDTO(
    val id: Long,
    val orderTime: Date,
    val orderStatus: OrderStatus,
    val address: String,
    val totalPrice: Int,
    val cancelReason: String? = null,
    var isOnlinePayment: Boolean ,
    var paymentStatus: PaymentStatus,
    val orderItemDTOList: List<OrderItemDTO>
)
