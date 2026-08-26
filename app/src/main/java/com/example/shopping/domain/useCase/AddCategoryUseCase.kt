package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repo: Repo
) {
    fun addCategory(category: CategoryDataModels): Flow<ResultState<String>> {
        return repo.addCategory(category)
    }
}
