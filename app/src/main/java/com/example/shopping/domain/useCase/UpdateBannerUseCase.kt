package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateBannerUseCase @Inject constructor(
    private val repo: Repo
) {
    fun updateBanner(bannerId: String, banner: BannerDataModels): Flow<ResultState<String>> {
        return repo.updateBanner(bannerId, banner)
    }
}
