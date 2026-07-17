package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class AddtoCardUseCase @Inject constructor(private val repo: Repo){

fun addtoCart(cartDataModels : CartDataModels): Flow<ResultState<String>> {

    return repo.addToCart(cartDataModels)

    }
}