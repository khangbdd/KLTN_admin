package com.example.aposs_admin.model.dto;

data class OrderItemDTO(
    val id: Long,
    val cartId: Long,
    val setId: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val property: String,
)
