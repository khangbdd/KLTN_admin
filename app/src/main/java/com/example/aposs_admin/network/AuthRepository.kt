package com.example.aposs_admin.network

import android.content.Context
import com.example.aposs_admin.model.dto.SignInDTO
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.network.service.AuthService
import com.example.aposs_buyer.responsitory.database.AccountDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val authService: AuthService by lazy {
        RetrofitInstance.retrofit.create(AuthService::class.java)
    }

    suspend fun signIn(email: String, password: String): Response<TokenDTO> {
        val signInDTO = SignInDTO(email, password)
        return authService.signIn(signInDTO)
    }

    suspend fun getAccessTokenFromRefreshToken(refreshToken: String): Response<String> {
        return authService.getAccessToken(refreshToken)
    }

    suspend fun resentConfirmEmail(email: String): Response<String> {
        return authService.resentConfirmEmail(email)
    }

    suspend fun loadNewAccessTokenSuccess(): Boolean{
        var isSuccess = false;
        val currentRefreshToken = getCurrentRefreshTokenFromRoom()
        if(currentRefreshToken!=null){
            val newAccessTokenResponse = getAccessTokenFromRefreshToken(currentRefreshToken)
            if(newAccessTokenResponse.isSuccessful){
                isSuccess = true
                updateAccessToken(newAccessTokenResponse.body()!!)
            }else{
                isSuccess = false
            }
        }
        return isSuccess
    }

    //Account with room database
    private fun updateAccessToken(newAccessToken: String) {
        AccountDatabase.getInstance(context).accountDao.updateAccessToken(newAccessToken)
    }

    fun getAccount(): com.example.aposs_admin.local_storage.entity.Account? {
        return AccountDatabase.getInstance(context).accountDao.getAccount()
    }

    fun getCurrentAccessTokenFromRoom(): String? {
        val currentAccount = AccountDatabase.getInstance(context).accountDao.getAccount()
        return if (currentAccount != null) {
            "${currentAccount.tokenType} ${currentAccount.accessToken}"
        } else {
            null;
        }
    }
    private fun getCurrentRefreshTokenFromRoom(): String? {
        return AccountDatabase.getInstance(context).accountDao.getAccount()?.refreshToken
    }

}