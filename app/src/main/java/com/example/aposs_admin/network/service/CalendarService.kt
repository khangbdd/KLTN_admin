package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.CalendarItem
import com.example.aposs_admin.model.dto.DefaultCalendarDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface CalendarService {

    @GET("/calendars")
    suspend fun getDefaultCalendars(): Response<DefaultCalendarDTO>

    @GET("/calendars/options")
    suspend fun getCalendarsList(
        @Body month: Int,
        @Body year: Int): Response<List<CalendarItem>>

    @PUT("/calendars")
    suspend fun updateDateStatus(
        @Body calendarItem: CalendarItem): Response<Unit>

}