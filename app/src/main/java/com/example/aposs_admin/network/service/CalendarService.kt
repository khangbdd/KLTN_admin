package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.CalendarItem
import com.example.aposs_admin.model.dto.CalendarItemDTO
import com.example.aposs_admin.model.dto.DefaultCalendarDTO
import retrofit2.Response
import retrofit2.http.*

interface CalendarService {

    @GET("calendar/request-data-first-time")
    suspend fun getDefaultCalendars(
    ): Response<DefaultCalendarDTO>

    @GET("calendar")
    suspend fun getCalendarsList(
        @Query("month") month: Int,
        @Query("year") year: Int): Response<List<CalendarItemDTO>>

    @PUT("calendar/{date}")
    suspend fun updateDateStatus(
        @Path("date") date: String,
        @Body calendarItem: CalendarItemDTO,
        @Header("Authorization") accessToken: String?): Response<Unit>

}