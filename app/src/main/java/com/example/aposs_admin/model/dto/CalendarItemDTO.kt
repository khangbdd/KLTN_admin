package com.example.aposs_admin.model.dto

import com.example.aposs_admin.model.CalendarItem
import java.text.SimpleDateFormat
import java.util.Calendar

data class CalendarItemDTO(
    val day: String,
    val isOff: Boolean,
    val isNationalHoliday: Boolean,
    val isLocalHoliday: Boolean,
    val isCurrentDay: Boolean,
    val description: String,
) {
    fun toCalendarItem() : CalendarItem{
        val simpleDateFormat = SimpleDateFormat("dd-mm-yyyy")
        val date = simpleDateFormat.parse(day)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        return CalendarItem(
            date = calendar.get(Calendar.DATE),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR),
            isOff = isOff,
            isNationalHoliday = isNationalHoliday,
            isLocalHoliday = isLocalHoliday,
            isCurrentDay = isCurrentDay,
            description =description,
        )
    }
}