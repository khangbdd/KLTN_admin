package com.example.aposs_admin.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.example.aposs_admin.util.OrderStatus
import com.example.aposs_admin.util.PaymentStatus
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Order(
    var id: Long,
    var orderTime: Date,
    var status: OrderStatus,
    var address: String,
    var paymentStatus: PaymentStatus,
    var isOnlinePayment: Boolean,
    var billingItems: ArrayList<OrderBillingItem>,
    var totalPrice: Int
) : Parcelable {
    var cancelReason = ""

    fun totalPriceToString(): String {
        val formatter = DecimalFormat("#,###")
        val formattedNumber = formatter.format(totalPrice.toLong())
        return "$formattedNumber VNĐ"
    }

    @get:SuppressLint("SimpleDateFormat")
    val timeString: String
        get() {
            val simpleDate = SimpleDateFormat("HH:mm dd/MM/yyyy")
            return simpleDate.format(orderTime)
        }
    val statusString: String
        get() = status.toString()

    val payingStatusString: String
        get() {
            if (paymentStatus == PaymentStatus.Completed) {
                return "Đã thanh toán"
            }
            if (paymentStatus == PaymentStatus.Waiting) {
                return "Chờ xác nhận"
            }
            return "Chưa thanh toán"
        }
    val payingTypeString: String
        get() {
            if (isOnlinePayment) {
                return "Thanh toán trực tuyến"
            }
            return "Thanh toán khi nhận hàng"
        }
}