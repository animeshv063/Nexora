package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val repo: Repo
) {
    fun placeOrder(
        productId: String,
        quantity: Int,
        address: String,
        paymentMethod: String
    ): Flow<ResultState<String>> {
        return repo.placeOrder(productId, quantity, address, paymentMethod)
    }
}
