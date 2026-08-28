package com.example.shopping.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class ProductDataModels(

    val name : String = "",
    val description : String = "",
    val price : String = "",
    val finalPrice : String = "",
    val category : String = "",
    val gender : String = "Men",
    val image : String = "",
    val date : Long = System.currentTimeMillis(),
    val createBy : String = "",
    val availableUnits : Int = 0,
    val initialUnits : Int = 0,
    val lastResetDay : Long = 0L,
    var productId : String = ""
)