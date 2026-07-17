package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserUseCase @Inject constructor(private val repo: Repo){

    fun getUserById(uid : String): Flow<ResultState<UserDataParent>> {

        return repo.getuserById(uid)

    }
}