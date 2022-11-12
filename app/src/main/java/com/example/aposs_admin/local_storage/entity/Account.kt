package com.example.aposs_admin.local_storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class Account(
    @PrimaryKey
    var userName: String,
    var password: String,
    var accessToken: String,
    var tokenType: String,
    var refreshToken: String,
)
