package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val repo: Repo
) {
    fun cancelOrder(orderId: String, productId: String, quantity: Int): Flow<ResultState<String>> {
        return repo.cancelOrder(orderId, productId, quantity)
    }
}
