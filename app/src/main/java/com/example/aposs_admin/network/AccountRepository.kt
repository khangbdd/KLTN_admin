package com.example.aposs_admin.network

import com.example.aposs_admin.network.service.AccountService
import com.google.android.gms.auth.api.identity.SignInPassword
import retrofit2.Response
import javax.inject.Inject

class AccountRepository @Inject constructor() {

    private val accountService: AccountService by lazy {
        RetrofitInstance.retrofit.create(AccountService::class.java)
    }

    fun getAllAccount(accessToken: String): Response<List<String>> {
        return accountService.getAllAccount(accessToken)
    }

    fun changePassword(account: String, password: String, accessToken: String): Response<Unit> {
        return accountService.changePassword(account, password, accessToken)
    }

    fun deleteAccount(account: String, accessToken: String): Response<Unit> {
        return accountService.deleteAccount(account, accessToken)
    }

    fun addNewAccount(account: String, password: String, accessToken: String): Response<Unit> {
        return accountService.addNewAccount(account, password, accessToken)
    }
}