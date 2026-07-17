package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateUserUseCase @Inject constructor(private val repo: Repo){

    fun createUser(userData : UserData): Flow<ResultState<String>> {

        return repo.registerUserWithEmailAndPassword(userData)

    }
}