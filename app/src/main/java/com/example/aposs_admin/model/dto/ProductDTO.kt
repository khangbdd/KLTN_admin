package com.example.aposs_admin.model.dto

data class ProductDTO (
    val id: Long,
    val image: String,
    val name: String,
    val price: Int,
    val purchased: Int,
)