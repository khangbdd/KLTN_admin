package com.example.aposs_admin.model.dto

import com.example.aposs_admin.model.CalendarItem

data class DefaultCalendarDTO(
    val listCalendar: List<CalendarItem>,
    val minYear:Int,
    val minMonth: Int,
)
