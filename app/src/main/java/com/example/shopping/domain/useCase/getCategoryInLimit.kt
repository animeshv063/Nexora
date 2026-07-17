package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class getCategoryInLimit @Inject constructor(private val repo: Repo){

    fun getCategoryInLimited(): Flow<ResultState<List<CategoryDataModels>>> {

        return repo.getCategoriesInLimited()

    }
}