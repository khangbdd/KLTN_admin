package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.AccountDTO
import com.example.aposs_admin.model.dto.SignInDTO
import retrofit2.Response
import retrofit2.http.*

interface AccountService {

    @GET("accounts")
    suspend fun getAllAccount(
        @Header("Authorization") accessToken: String?
    ): Response<List<String>>

    @PUT("accounts/update-password/{email}")
    suspend fun changePassword(
        @Path("email") email: String,
        @Body newPassword : String,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @POST("accounts")
    suspend fun addNewAccount(
        @Body accountDTO: AccountDTO,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>

    @DELETE("accounts/{account}")
    suspend fun deleteAccount(
        @Path("account") account: String,
        @Header("Authorization") accessToken: String?
    ): Response<Unit>
}