package com.example.shopping.domain.repo

import android.net.Uri
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun registerUserWithEmailAndPassword(userData : UserData) : Flow<ResultState<String>>
    fun loginUserWithEmailAndPassword(userData : UserData) : Flow<ResultState<String>>
    fun getuserById(uid : String) : Flow<ResultState<UserDataParent>>
    fun updateUserData(userDataParent : UserDataParent) : Flow<ResultState<String>>
    fun userProfileImage(uri : Uri) : Flow<ResultState<String>>
    fun getCategoriesInLimited() : Flow<ResultState<List<CategoryDataModels>>>
    fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>>
    fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>>
    fun getProductById(productId : String) : Flow<ResultState<ProductDataModels>>
    fun addToCart(cartDataModules : CartDataModels) : Flow<ResultState<String>>
    fun removeFromCart(cartId: String) : Flow<ResultState<String>>
    fun addToFav(productDataModels : ProductDataModels): Flow<ResultState<String>>
    fun removeFromFav(productId: String): Flow<ResultState<String>>
    fun getAllFav() : Flow<ResultState<List<ProductDataModels>>>
    fun getCart() : Flow<ResultState<List<CartDataModels>>>
    fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>>
    fun getCheckout(productId : String) : Flow<ResultState<ProductDataModels>>
    fun getBanner() : Flow<ResultState<List<BannerDataModels>>>
    fun getSpecificCategoryItems(categoryName:String): Flow<ResultState<List<ProductDataModels>>>
    fun getAllSuggestedProducts():Flow<ResultState<List<ProductDataModels>>>
    fun deleteUserAccount(uid: String, password: String = ""): Flow<ResultState<String>>
    fun loginWithGoogle(idToken: String): Flow<ResultState<String>>
}

