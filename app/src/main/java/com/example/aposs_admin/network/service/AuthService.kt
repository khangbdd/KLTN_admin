package com.example.aposs_admin.network.service

import com.example.aposs_admin.model.dto.SignInDTO
import com.example.aposs_admin.model.dto.TokenDTO
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("auth/seller-sign-in")
    suspend fun signIn(
        @Body signInDTO: SignInDTO
    ): Response<TokenDTO>

    @POST("auth/access-token")
    suspend fun getAccessToken(
        @Body refreshToken: String
    ): Response<String>

    @GET("auth/resent-confirm")
    suspend fun resentConfirmEmail (
        @Query("email") email: String
    ):Response<String>
}