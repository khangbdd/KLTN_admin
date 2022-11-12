package com.example.aposs_admin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat

@Parcelize
class OrderBillingItem(
    var id: Long = -1,
    var cartId: Long = -1,
    var productId: Long,
    var image: Image = Image(""),
    var name: String= "",
    var price: Int,
    var amount: Int,
    var property: String = ""
) : Parcelable {

    fun priceToString(): String {
        val formatter = DecimalFormat("#,###")
        val formattedNumber = formatter.format(price.toLong())
        return "$formattedNumber VNĐ"
    }

    fun amountToString(): String {
        return "x$amount"
    }

    val totalPrice: Int
        get() = amount * price
    val totalPriceToString: String
        get() {
            val formatter = DecimalFormat("#,###")
            val formattedNumber = formatter.format(totalPrice.toLong())
            return "$formattedNumber VNĐ"
        }
}