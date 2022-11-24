package com.example.aposs_admin.model

import com.example.aposs_admin.model.Image

data class DetailCategory(val id: Long,
                          var name: String,
                          val totalPurchase: Int,
                          val totalProduct: Int,
                          val rating: Float,
                          var images: MutableList<Image>)
{

    constructor(id: Long, name: String, images: MutableList<Image>) : this(
        id, name, 0, 0,0F,images
    )

    fun displayToTalPurchase(): String
    {
        return "$totalPurchase purchased"
    }

    fun displayToTalProduct(): String
    {
        return "$totalProduct products"
    }
}