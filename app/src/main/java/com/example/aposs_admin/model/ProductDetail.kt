package com.example.aposs_admin.model

import java.text.DecimalFormat

data class ProductDetail(
    val id: Long,
    val name: String,
    val price: Int,
    val purchase: Int,
    var isFavorite: Boolean,
    val description: String,
    val availableQuantities: Int,
    val kind: String,
){
    fun priceToString(): String{
        val formatter = DecimalFormat("#,###")
        val formattedNumber: String = formatter.format(price)
        return "$formattedNumber VNĐ"
    }
    fun totalPurchaseToString(): String{
        return "$purchase purchased"
    }
//    fun toTalReviewToString(): String{
//        return "($totalReview reviews)"
//    }
}
