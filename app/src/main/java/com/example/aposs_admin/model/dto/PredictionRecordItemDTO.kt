package com.example.aposs_admin.model.dto


data class PredictionRecordItemDTO (
    val date: String? = null,
    val sale: Int = 0,
    val lower: Double? = null,
    val upper: Double? = null,
    val delta: Double? = null
)