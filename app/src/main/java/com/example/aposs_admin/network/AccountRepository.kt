package com.example.aposs_admin.network

import com.example.aposs_admin.model.dto.AccountDTO
import com.example.aposs_admin.network.service.AccountService
import com.google.android.gms.auth.api.identity.SignInPassword
import retrofit2.Response
import javax.inject.Inject

class AccountRepository @Inject constructor() {

    private val accountService: AccountService by lazy {
        RetrofitInstance.retrofit.create(AccountService::class.java)
    }

    suspend fun getAllAccount(accessToken: String): Response<List<String>> {
        return accountService.getAllAccount(accessToken)
    }

    suspend fun changePassword(email: String, password: String, accessToken: String): Response<Unit> {
        return accountService.changePassword(email, password, accessToken)
    }

    suspend fun deleteAccount(account: String, accessToken: String): Response<Unit> {
        return accountService.deleteAccount(account, accessToken)
    }

    suspend fun addNewAccount(accountDTO: AccountDTO, accessToken: String): Response<Unit> {
        return accountService.addNewAccount(accountDTO, accessToken)
    }
}