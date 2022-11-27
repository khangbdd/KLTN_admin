package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.CalendarItem
import com.example.aposs_admin.model.dto.CalendarItemDTO
import com.example.aposs_admin.model.dto.DefaultCalendarDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface CalendarService {

    @GET("/calendars")
    suspend fun getDefaultCalendars(
        @Header("Authorization") accessToken: String?
    ): Response<DefaultCalendarDTO>

    @GET("/calendars/options")
    suspend fun getCalendarsList(
        @Body month: Int,
        @Body year: Int,
        @Header("Authorization") accessToken: String?): Response<List<CalendarItemDTO>>

    @PUT("/calendars")
    suspend fun updateDateStatus(
        @Body calendarItem: CalendarItemDTO,
        @Header("Authorization") accessToken: String?): Response<Unit>

}