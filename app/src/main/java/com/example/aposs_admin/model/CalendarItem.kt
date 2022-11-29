package com.example.aposs_admin.model

import android.os.Parcelable
import com.example.aposs_admin.model.dto.CalendarItemDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalendarItem(
    val id: Long,
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
        var dateFormat = date.toString()
        if (date < 10) {
            dateFormat = "0$dateFormat"
        }
        var monthFormat = month.toString()
        if (month < 10) {
            monthFormat = "0$monthFormat"
        }
        return CalendarItemDTO(
            id,
            "$dateFormat-$monthFormat-$year",
            isOff,
            isNationalHoliday,
            isLocalHoliday,
            isCurrentDay,
            description
        )
    }
}
