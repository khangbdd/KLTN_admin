package com.example.aposs_admin.model.dto

data class NewProductDTO (
     val description: String? = null,
     val name: String? = null,
     val price : Int= 0,
     val quantity: Int = 0,
     val kindId: Long = 0,
)