package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.SignInDTO
import retrofit2.Response
import retrofit2.http.*

interface AccountService {

    @GET("/accounts")
    fun getAllAccount(
        @Header("Authorization") accessToken: String?
    ): Response<List<String>>

    @PUT("/accounts/{account}")
    fun changePassword(
        @Path("account") account: String,
        @Body password: String,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @POST("/accounts/{account}")
    fun addNewAccount(
        @Path("account") account: String,
        @Body password: String,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @PUT("/accounts/{account}")
    fun deleteAccount(
        @Path("account") account: String,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>
}