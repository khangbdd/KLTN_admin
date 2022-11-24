package com.example.aposs_admin.model

data class CalendarItem(
    val date: Int,
    val month: Int,
    val year: Int,
    val isOff: Boolean,
    val isNationalHoliday: Boolean,
    val isLocalHoliday: Boolean
)
