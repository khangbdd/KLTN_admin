package com.example.aposs_admin.model

import android.os.Parcelable
import com.example.aposs_admin.model.dto.CalendarItemDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalendarItem(
    val date: Int,
    val month: Int,
    val year: Int,
    var isOff: Boolean,
    val isNationalHoliday: Boolean,
    var isLocalHoliday: Boolean,
    var isCurrentDay: Boolean,
    var description: String,
) : Parcelable {
    fun toCalendarItemDTO(): CalendarItemDTO {
        return CalendarItemDTO(
            "$date-$month-$year",
            isOff,
            isNationalHoliday,
            isLocalHoliday,
            isCurrentDay,
            description
        )
    }
}
