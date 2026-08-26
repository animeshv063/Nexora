package com.example.shopping.domain.models

data class CategoryDataModels (
    var categoryId: String = "",
    var name : String = "",
    var date : Long = System.currentTimeMillis(),
    var createBy : String = "Animesh",
    var categoryImage : String = ""
)