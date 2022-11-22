package com.example.aposs_admin.model.dto


data class KindDTO(
    var id: Long = -1L,
    var name:String = "",
    var totalPurchases: Int = 0,
    var totalProducts: Int = 0,
    var rating: Float = 3.0F,
    var image: String = "",
    var products: List<ProductDTO> = mutableListOf(),
    var category: Long  = 3L,
)
