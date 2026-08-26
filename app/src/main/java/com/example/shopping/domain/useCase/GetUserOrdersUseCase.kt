package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.OrderDataModel
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserOrdersUseCase @Inject constructor(
    private val repo: Repo
) {
    fun getUserOrders(): Flow<ResultState<List<OrderDataModel>>> {
        return repo.getUserOrders()
    }
}
