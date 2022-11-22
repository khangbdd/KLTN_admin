package com.example.aposs_admin.model


data class Subcategory(
    var id: Long,
    var name: String,
    var Products: List<HomeProduct>,
    var image: Image,
    var categoryName: String,
)
