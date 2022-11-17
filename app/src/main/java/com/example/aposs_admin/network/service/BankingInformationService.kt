package com.example.aposs_admin.network.service


import com.example.aposs_admin.model.dto.BankingInformationDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface BankingInformationService {

    @GET("banking-information")
    suspend fun getInformationService(): Response<BankingInformationDTO>
    
    @PUT("banking-information")
    suspend fun updateBankingInformation(
        @Header("Authorization") accessToken: String,
        @Body newBankingInformationDTO: BankingInformationDTO
    ): Response<Unit>
}