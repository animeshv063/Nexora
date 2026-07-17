package com.example.shopping.presentation.navigation

import kotlinx.serialization.Serializable
sealed class SubNavigation{

    @Serializable
    object LoginSignUpScreen : SubNavigation()

    @Serializable
    object MainHomeScreen : SubNavigation()




}

sealed class Routes{

    @Serializable
    object LoginScreen

    @Serializable
    object SignUpScreen

    @Serializable
    object HomeScreen

    @Serializable
    object ProfileScreen

    @Serializable
    object WishListScreen

    @Serializable
    object CartScreen

    @Serializable
    data class CheckoutScreen(val productId : String)

    @Serializable
    object PayScreen

    @Serializable
    object SeeAllProductScreen

    @Serializable
    data class EachProductDetailScreen(val products : String)

    @Serializable
    object AllCategoryScreen

    @Serializable
    data class EachCategoryItemsScreens(val categoryName : String)


}