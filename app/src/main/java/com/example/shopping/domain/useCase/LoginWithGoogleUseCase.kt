package com.example.shopping.domain.useCase

import com.example.shopping.common.ResultState
import com.example.shopping.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repo: Repo
) {
    fun loginWithGoogle(idToken: String): Flow<ResultState<String>> {
        return repo.loginWithGoogle(idToken)
    }
}
