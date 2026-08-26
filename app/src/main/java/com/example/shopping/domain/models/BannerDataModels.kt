package com.example.shopping.domain.models

data class BannerDataModels(
    var bannerId: String = "",
    val name : String = "",
    val image : String = "",
    val date : Long = System.currentTimeMillis()
)
