package com.example.shopping.presentation.viewModels

import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserDataParent

data class ProfileScreenState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class SignUpScreen(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class LoginScreenState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class ProfileUpdateScreenState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class uploadUserProfileImageState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class AddtoCartState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class GetProductByIDState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class AddToFavState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : UserDataParent? = null
)

data class GetAllFavState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : List<ProductDataModels?> = emptyList()
)

data class GetCartState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : List<CartDataModels>? = emptyList()
)

data class GetAllCategoriesState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : List<CategoryDataModels?> = emptyList()
)

data class GetCheckoutState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : ProductDataModels? = null
)

data class GetSpecificCategoryItemsState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : List<ProductDataModels> = emptyList()
)

data class GetAllSuggestedProductsState(
    val isLoading :Boolean = false,
    val errorMessage :String? = null,
    val userData : List<ProductDataModels> = emptyList()
)




