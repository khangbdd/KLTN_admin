package com.example.aposs_admin.model

import com.example.aposs_admin.model.Image

data class Category(
    val id: Long,
    val name: String,
    val totalPurchase: Int,
    val totalProduct: Int,
    val rating: Float,
    val mainImage: Image
)
