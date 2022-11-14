package com.example.aposs_admin.model.dto


data class NewProductImageDTO (
     val imageUrl: String? = null,
     val priority: Int = 0,
     val productId: Long = 0,
)
