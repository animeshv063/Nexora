package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllCategoryUseCase @Inject constructor(private val repo: Repo){

    fun getAllCategoriesUseCase(): Flow<ResultState<List<CategoryDataModels>>> {

        return repo.getAllCategories()

    }
}
