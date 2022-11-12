package com.example.aposs_admin.model

import com.example.aposs_admin.model.Image
import com.squareup.moshi.Json
import java.text.DecimalFormat
import java.text.NumberFormat

data class HomeProduct(
    val id: Long,
    val image: Image,
    val name: String,
    val price: Int,
    val purchased: Int,
){
    fun priceToString(): String{
        val formatter = DecimalFormat("#,###")
        val formattedNumber: String = formatter.format(price)
        return "$formattedNumber VNĐ"
    }
}