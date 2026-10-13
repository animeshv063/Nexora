package com.example.shopping.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.shopping.presentation.LoginScreen
import com.example.shopping.presentation.SignUpScreen
import com.example.shopping.presentation.screens.AdminDashboardScreen
import com.example.shopping.presentation.screens.AllCategoryScreen
import com.example.shopping.presentation.screens.CartScreen
import com.example.shopping.presentation.screens.CategoryProductsScreen
import com.example.shopping.presentation.screens.CheckoutScreen
import com.example.shopping.presentation.screens.ProductDetailScreen
import com.example.shopping.presentation.screens.SeeAllProductScreen
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.google.firebase.auth.FirebaseAuth

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: ShoppingAppViewModel = hiltViewModel()
    val auth = FirebaseAuth.getInstance()
    val startDestination: Any = if (auth.currentUser != null) SubNavigation.MainHomeScreen else SubNavigation.LoginSignUpScreen

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Dedicated status bar area matching the exact active theme color
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(DarkBg)
            )
            // Application content strictly starts below status bar - cannot touch or cross it
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable<SubNavigation.LoginSignUpScreen> {
                        val authNavController = rememberNavController()
                        NavHost(navController = authNavController, startDestination = Routes.LoginScreen) {
                            composable<Routes.LoginScreen> {
                                LoginScreen(
                                    viewModel = viewModel,
                                    onNavigateToSignUp = { authNavController.navigate(Routes.SignUpScreen) },
                                    onLoginSuccess = {
                                        navController.navigate(SubNavigation.MainHomeScreen) {
                                            popUpTo(SubNavigation.LoginSignUpScreen) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable<Routes.SignUpScreen> {
                                SignUpScreen(
                                    viewModel = viewModel,
                                    onNavigateToLogin = { authNavController.popBackStack() },
                                    onSignUpSuccess = {
                                        navController.navigate(SubNavigation.MainHomeScreen) {
                                            popUpTo(SubNavigation.LoginSignUpScreen) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    composable<SubNavigation.MainHomeScreen> {
                        MainContainer(
                            mainAppNavController = navController,
                            viewModel = viewModel
                        )
                    }

                    composable<Routes.AllCategoryScreen> {
                        AllCategoryScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCategoryClick = { catName ->
                                navController.navigate(Routes.EachCategoryItemsScreens(categoryName = catName))
                            }
                        )
                    }

                    composable<Routes.EachCategoryItemsScreens> { backStackEntry ->
                        val route = backStackEntry.toRoute<Routes.EachCategoryItemsScreens>()
                        CategoryProductsScreen(
                            categoryName = route.categoryName,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onProductClick = { pId -> navController.navigate(Routes.EachProductDetailScreen(products = pId)) }
                        )
                    }

                    composable<Routes.SeeAllProductScreen> {
                        SeeAllProductScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onProductClick = { pId -> navController.navigate(Routes.EachProductDetailScreen(products = pId)) }
                        )
                    }

                    composable<Routes.EachProductDetailScreen> { backStackEntry ->
                        val route = backStackEntry.toRoute<Routes.EachProductDetailScreen>()
                        ProductDetailScreen(
                            productId = route.products,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToCart = { navController.navigate(Routes.CartScreen) },
                            onBuyNowClick = { pId, qty, size ->
                                navController.navigate(Routes.CheckoutScreen(productId = pId, quantity = qty, size = size))
                            }
                        )
                    }

                    composable<Routes.CheckoutScreen> { backStackEntry ->
                        val route = backStackEntry.toRoute<Routes.CheckoutScreen>()
                        CheckoutScreen(
                            productId = route.productId,
                            quantity = route.quantity,
                            size = route.size,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onOrderSuccess = {
                                navController.navigate(SubNavigation.MainHomeScreen) {
                                    popUpTo(SubNavigation.MainHomeScreen) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Routes.CartScreen> {
                        CartScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCheckoutClick = { pId -> navController.navigate(Routes.CheckoutScreen(productId = pId)) },
                            isStandalone = true
                        )
                    }

                    composable<Routes.AdminDashboardScreen> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}