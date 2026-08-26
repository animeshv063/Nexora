package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetUserOrdersUseCase @Inject constructor(
    private val repo: Repo
) {
    fun resetUserOrders(): Flow<ResultState<String>> {
        return repo.resetUserOrders()
    }
}
