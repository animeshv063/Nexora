package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetBannerUseCase @Inject constructor(private val repo: Repo){

    fun getBannerUseCase(): Flow<ResultState<List<BannerDataModels>>> {

        return repo.getBanner()

    }
}