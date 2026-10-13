package com.example.shopping.data.repo

import android.net.Uri
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class RepoImplementation @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseFirestore: FirebaseFirestore,
    @dagger.hilt.android.qualifiers.ApplicationContext val context: android.content.Context
) : Repo
{
    // --- User & Auth Operations ---
    override fun registerUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        registerUserWithEmailAndPasswordImpl(firebaseAuth, firebaseFirestore, userData)

    override fun loginUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        loginUserWithEmailAndPasswordImpl(firebaseAuth, userData)

    override fun getuserById(uid: String): Flow<ResultState<UserDataParent>> =
        getuserByIdImpl(firebaseAuth, firebaseFirestore, uid)

    override fun updateUserData(userDataParent: UserDataParent): Flow<ResultState<String>> =
        updateUserDataImpl(firebaseFirestore, userDataParent)

    override fun userProfileImage(uri: Uri): Flow<ResultState<String>> =
        userProfileImageImpl(firebaseAuth, uri)

    override fun deleteUserAccount(uid: String, password: String): Flow<ResultState<String>> =
        deleteUserAccountImpl(firebaseAuth, firebaseFirestore, uid, password)

    override fun loginWithGoogle(idToken: String): Flow<ResultState<String>> =
        loginWithGoogleImpl(firebaseAuth, firebaseFirestore, idToken)

    // --- Product & Catalog Operations ---
    override fun getCategoriesInLimited(): Flow<ResultState<List<CategoryDataModels>>> =
        getCategoriesInLimitedImpl(firebaseFirestore)

    override fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>> =
        getProductsInLimitedImpl(firebaseFirestore)

    override fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>> =
        getAllProductsImpl(firebaseFirestore)

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModels>> =
        getProductByIdImpl(firebaseFirestore, productId)

    override fun addToCart(cartDataModules: CartDataModels): Flow<ResultState<String>> =
        addToCartImpl(firebaseAuth, firebaseFirestore, cartDataModules)

    override fun removeFromCart(cartId: String): Flow<ResultState<String>> =
        removeFromCartImpl(firebaseAuth, firebaseFirestore, cartId)

    override fun addToFav(productDataModels: ProductDataModels): Flow<ResultState<String>> =
        addToFavImpl(firebaseAuth, firebaseFirestore, productDataModels)

    override fun removeFromFav(productId: String): Flow<ResultState<String>> =
        removeFromFavImpl(firebaseAuth, firebaseFirestore, productId)

    override fun getAllFav(): Flow<ResultState<List<ProductDataModels>>> =
        getAllFavImpl(firebaseAuth, firebaseFirestore)

    override fun getCart(): Flow<ResultState<List<CartDataModels>>> =
        getCartImpl(firebaseAuth, firebaseFirestore)

    override fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>> =
        getAllCategoriesImpl(firebaseFirestore)

    override fun getCheckout(productId: String): Flow<ResultState<ProductDataModels>> =
        getCheckoutImpl(firebaseFirestore, productId)

    override fun getBanner(): Flow<ResultState<List<BannerDataModels>>> =
        getBannerImpl(firebaseFirestore)

    override fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>> =
        getSpecificCategoryItemsImpl(firebaseFirestore, categoryName)

    override fun getAllSuggestedProducts(): Flow<ResultState<List<ProductDataModels>>> =
        getAllSuggestedProductsImpl(firebaseAuth, firebaseFirestore)

    // --- Order Operations ---
    override fun placeOrder(productId: String, quantity: Int, address: String, paymentMethod: String): Flow<ResultState<String>> =
        placeOrderImpl(firebaseAuth, firebaseFirestore, context, productId, quantity, address, paymentMethod)

    override fun cancelOrder(orderId: String, productId: String, quantity: Int): Flow<ResultState<String>> =
        cancelOrderImpl(firebaseAuth, firebaseFirestore, context, orderId, productId, quantity)

    override fun getUserOrders(): Flow<ResultState<List<com.example.shopping.domain.models.OrderDataModel>>> =
        getUserOrdersImpl(firebaseAuth, firebaseFirestore, context)

    override fun resetUserOrders(): Flow<ResultState<String>> =
        resetUserOrdersImpl(firebaseAuth, firebaseFirestore, context)

    // --- Admin CRUD Operations ---
    override fun addProduct(product: ProductDataModels): Flow<ResultState<String>> =
        addProductImpl(firebaseAuth, firebaseFirestore, product)

    override fun updateProduct(product: ProductDataModels): Flow<ResultState<String>> =
        updateProductImpl(firebaseAuth, firebaseFirestore, product)

    override fun deleteProduct(productId: String): Flow<ResultState<String>> =
        deleteProductImpl(firebaseAuth, firebaseFirestore, productId)

    override fun addCategory(category: CategoryDataModels): Flow<ResultState<String>> =
        addCategoryImpl(firebaseAuth, firebaseFirestore, category)

    override fun updateCategory(categoryId: String, category: CategoryDataModels): Flow<ResultState<String>> =
        updateCategoryImpl(firebaseAuth, firebaseFirestore, categoryId, category)

    override fun deleteCategory(categoryId: String): Flow<ResultState<String>> =
        deleteCategoryImpl()

    override fun addBanner(banner: BannerDataModels): Flow<ResultState<String>> =
        addBannerImpl(firebaseAuth, firebaseFirestore, banner)

    override fun updateBanner(bannerId: String, banner: BannerDataModels): Flow<ResultState<String>> =
        updateBannerImpl(firebaseAuth, firebaseFirestore, bannerId, banner)

    override fun deleteBanner(bannerId: String): Flow<ResultState<String>> =
        deleteBannerImpl(firebaseAuth, firebaseFirestore, bannerId)
}
