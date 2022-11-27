package com.example.aposs_admin.model.dto

import com.example.aposs_admin.model.CalendarItem

data class DefaultCalendarDTO(
    val listCalendar: List<CalendarItemDTO>,
    val minYear:Int,
    val minMonth: Int,
    val maxYear:Int,
    val maxMonth: Int,
    val currentMonth: Int,
    val currentYear: Int
) {
    fun getListCalendarItem(): MutableList<CalendarItem> {
        val listCalendarItem = mutableListOf<CalendarItem>()
        for (calendarDTO in listCalendar)
        {
            listCalendarItem.add(calendarDTO.toCalendarItem())
        }
        return listCalendarItem
    }
}
