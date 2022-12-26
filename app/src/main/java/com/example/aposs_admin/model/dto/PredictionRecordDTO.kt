package com.example.aposs_admin.model.dto

import com.example.aposs_admin.util.PredictionStatus


data class PredictionRecordDTO (
    var id: Long = 0,
    var name: String? = null,
    var createDate: String? = null,
    var productID: Long = -1L,
    var productName: String? = null,
    var fromDate: String? = null,
    var toDate: String? = null,
    var isActive: Boolean = true,
    var subcategoryId: Long = 0,
    var subcategoryName: String? = null,
    var predictionStatus: PredictionStatus? = null
)
{
    fun getFullFromToDate(): String {
        return "$fromDate - $toDate"
    }
}