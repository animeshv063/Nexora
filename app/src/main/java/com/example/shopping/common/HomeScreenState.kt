package com.example.shopping.common

import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels

data class HomeScreenState (

    val isLoading : Boolean = true,
    val errorMessage : String? = null,
    val categories : List<CategoryDataModels>? = null,
    val products : List<ProductDataModels>? = null,
    val banners : List<BannerDataModels>? = null

)