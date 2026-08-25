package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveFromFavUseCase @Inject constructor(
    private val repo: Repo
) {
    fun removeFromFav(productId: String): Flow<ResultState<String>> {
        return repo.removeFromFav(productId)
    }
}
