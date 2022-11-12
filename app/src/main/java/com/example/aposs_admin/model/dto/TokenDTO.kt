package com.example.aposs_admin.model.dto

data class TokenDTO(
    var accessToken: String,
    var tokenType: String,
    var refreshToken: String,
){
    fun getFullAccessToken(): String{
        return "$tokenType $accessToken"
    }
}