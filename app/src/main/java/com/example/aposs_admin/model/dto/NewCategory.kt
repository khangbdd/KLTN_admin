package com.example.aposs_admin.model.dto

data class NewCategory(
    val id: Long,
    val name: String,
    val images: List<String>
)
