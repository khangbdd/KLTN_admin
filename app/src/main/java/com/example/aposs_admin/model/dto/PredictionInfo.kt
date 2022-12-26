package com.example.aposs_admin.model.dto
data class PredictionInfo (
    val frequency: String = "D",
    val productId: Long = -1L,
    val subcategoryId: Long = 0,
    val horizon: Int = 0
)