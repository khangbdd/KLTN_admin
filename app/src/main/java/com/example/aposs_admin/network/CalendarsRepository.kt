package com.example.aposs_admin.network

import com.example.aposs_admin.model.CalendarItem
import com.example.aposs_admin.model.dto.CalendarItemDTO
import com.example.aposs_admin.model.dto.DefaultCalendarDTO
import com.example.aposs_admin.network.service.CalendarService
import retrofit2.Response
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.PUT
import javax.inject.Inject

class CalendarsRepository @Inject constructor() {

    private val calendarService: CalendarService by lazy {
        RetrofitInstance.retrofit.create(CalendarService::class.java)
    }

    suspend fun getDefaultCalendars(accessToken: String): Response<DefaultCalendarDTO> {
        return calendarService.getDefaultCalendars()
    }

    suspend fun getCalendarsList(month: Int, year: Int, accessToken: String): Response<List<CalendarItemDTO>> {
        return calendarService.getCalendarsList(month, year)
    }

    suspend fun updateDateStatus(calendarItem: CalendarItemDTO, accessToken: String): Response<Unit> {
        return calendarService.updateDateStatus(calendarItem.day,calendarItem, accessToken)
    }
}