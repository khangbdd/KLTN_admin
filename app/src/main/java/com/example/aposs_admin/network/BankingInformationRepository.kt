package com.example.aposs_admin.network


import com.example.aposs_admin.model.dto.BankingInformationDTO
import com.example.aposs_admin.network.service.BankingInformationService
import retrofit2.Response
import javax.inject.Inject

class BankingInformationRepository @Inject constructor() {
    private val bankingInformationService: BankingInformationService by lazy {
        RetrofitInstance.retrofit.create(BankingInformationService::class.java)
    }

    suspend fun getBankingInformationData(): Response<BankingInformationDTO>{
        return bankingInformationService.getInformationService()
    }

    suspend fun updateBankingInformation(accessToken: String, newInformationDTO: BankingInformationDTO): Response<Unit>{
        return bankingInformationService.updateBankingInformation(accessToken, newInformationDTO)
    }
}