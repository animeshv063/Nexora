package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repo: Repo
) {
    fun deleteCategory(categoryId: String): Flow<ResultState<String>> {
        return repo.deleteCategory(categoryId)
    }
}
