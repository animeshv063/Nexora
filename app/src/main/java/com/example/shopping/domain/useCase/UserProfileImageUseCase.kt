package com.example.shopping.domain.useCase

import android.net.Uri
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.repo.Repo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class userProfileImageUseCase @Inject constructor(private val repo: Repo){

    fun userProfileImage(uri : Uri): Flow<ResultState<String>> {

        return repo.userProfileImage(uri)

    }
}