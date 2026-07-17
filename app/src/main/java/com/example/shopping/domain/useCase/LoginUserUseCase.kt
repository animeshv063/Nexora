package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class LoginUserUseCase @Inject constructor(private val repo: Repo){

    fun loginUser(userData : UserData): Flow<ResultState<String>> {

        return repo.loginUserWithEmailAndPassword(userData)

    }
}