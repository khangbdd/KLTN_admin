package com.example.aposs_admin.network

import com.example.aposs_admin.model.CalendarItem
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

    suspend fun getDefaultCalendars(): Response<DefaultCalendarDTO> {
        return calendarService.getDefaultCalendars()
    }

    suspend fun getCalendarsList(month: Int, year: Int): Response<List<CalendarItem>> {
        return calendarService.getCalendarsList(month, year)
    }

    suspend fun updateDateStatus(calendarItem: CalendarItem): Response<Unit> {
        return calendarService.updateDateStatus(calendarItem)
    }
}