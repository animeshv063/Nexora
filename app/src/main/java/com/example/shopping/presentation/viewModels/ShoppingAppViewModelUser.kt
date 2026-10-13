package com.example.shopping.presentation.viewModels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun ShoppingAppViewModel.loginWithGoogleToken(idToken: String) {
    viewModelScope.launch {
        loginWithGoogleUseCase.loginWithGoogle(idToken).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _googleSignInState.value = ResponseState(isLoading = true)
                is ResultState.Success -> _googleSignInState.value = ResponseState(data = result.data)
                is ResultState.Error -> _googleSignInState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.deleteAccount(uid: String, password: String = "") {
    viewModelScope.launch {
        deleteUserAccountUseCase.deleteUser(uid, password).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _deleteAccountState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _deleteAccountState.value = ResponseState(data = result.data)
                    _userProfileState.value = ResponseState()
                }
                is ResultState.Error -> _deleteAccountState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.updateProfile(userDataParent: UserDataParent) {
    viewModelScope.launch {
        updateUserDataUseCase.updateUserData(userDataParent).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _profileUpdateState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _profileUpdateState.value = ResponseState(data = result.data)
                    fetchUserProfile(userDataParent.nodeId)
                }
                is ResultState.Error -> _profileUpdateState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.uploadProfileImage(uri: Uri, uid: String, currentData: UserData) {
    viewModelScope.launch {
        userProfileImageUseCase.userProfileImage(uri).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _profileUpdateState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    val updatedUserData = currentData.copy(profileImage = result.data ?: "")
                    updateProfile(UserDataParent(nodeId = uid, userData = updatedUserData))
                }
                is ResultState.Error -> _profileUpdateState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.resetDeleteAccountState() {
    _deleteAccountState.value = ResponseState()
}

fun ShoppingAppViewModel.resetGoogleSignInState() {
    _googleSignInState.value = ResponseState()
}

fun ShoppingAppViewModel.resetProfileUpdateState() {
    _profileUpdateState.value = ResponseState()
}
