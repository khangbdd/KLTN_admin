package com.example.aposs_admin.model.dto

import android.util.Log
import com.example.aposs_admin.model.CalendarItem
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.Calendar

data class CalendarItemDTO(
    val id : Long = 0L,
    @Json(name = "date")val day: String,
    @Json(name ="dayOff") val isOff: Boolean,
    @Json(name ="nationalHoliday")val isNationalHoliday: Boolean,
    @Json(name ="localHoliday")val isLocalHoliday: Boolean,
    @Json(name ="currentDate")val isCurrentDay: Boolean,
    val description: String? = "",
) {
    fun toCalendarItem() : CalendarItem{
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = simpleDateFormat.parse(day)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        return CalendarItem(
            id = id,
            date = calendar.get(Calendar.DATE),
            month = calendar.get(Calendar.MONTH)+1,
            year = calendar.get(Calendar.YEAR),
            isOff = isOff,
            isNationalHoliday = isNationalHoliday,
            isLocalHoliday = isLocalHoliday,
            isCurrentDay = isCurrentDay,
            description =description ?: "",
        )
    }
}