package com.example.shopping.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.shopping.presentation.screens.HomeScreen
import com.example.shopping.presentation.screens.ProductDetailScreen
import com.example.shopping.presentation.screens.ProfileScreen
import com.example.shopping.presentation.screens.SeeAllProductScreen
import com.example.shopping.presentation.screens.WishlistScreen
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.ui.theme.DarkBg
import com.example.shopping.ui.theme.DarkCard
import com.example.shopping.ui.theme.OrangePrimary
import com.example.shopping.ui.theme.TextMuted
import com.example.shopping.ui.theme.TextWhite
import com.google.firebase.auth.FirebaseAuth

data class BottomBarItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: ShoppingAppViewModel = hiltViewModel()
    val auth = FirebaseAuth.getInstance()
    val startDestination: Any = if (auth.currentUser != null) SubNavigation.MainHomeScreen else SubNavigation.LoginSignUpScreen

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
                onCheckoutClick = { pId -> navController.navigate(Routes.CheckoutScreen(productId = pId)) }
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

@Composable
fun MainContainer(
    mainAppNavController: androidx.navigation.NavHostController,
    viewModel: ShoppingAppViewModel
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val items = listOf(
        BottomBarItem("Home", Icons.Filled.Home),
        BottomBarItem("WishList", Icons.Filled.Favorite),
        BottomBarItem("Cart", Icons.Filled.ShoppingCart),
        BottomBarItem("Profile", Icons.Filled.Person)
    )

    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isThreeButtonNav = navBarsBottom >= 30.dp
    val bottomNavElevation: Dp = if (isThreeButtonNav) navBarsBottom + 12.dp else navBarsBottom + 4.dp

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            Surface(
                color = DarkBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomNavElevation)
                    .height(64.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index
                        if (isSelected) {
                            // Active Orange Pill Shape matching screenshot
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(OrangePrimary)
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                                    .clickable { selectedIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = com.example.shopping.ui.theme.ButtonTextColor,
                                    modifier = Modifier.size(20.dp)
                                )

                            }

                        } else {
                            Text(
                                text = item.title,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { selectedIndex = index }
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { productId ->
                        mainAppNavController.navigate(Routes.EachProductDetailScreen(products = productId))
                    },
                    onCategoryClick = { categoryName ->
                        mainAppNavController.navigate(Routes.EachCategoryItemsScreens(categoryName = categoryName))
                    },
                    onSeeAllCategoriesClick = {
                        mainAppNavController.navigate(Routes.AllCategoryScreen)
                    },
                    onSeeAllProductsClick = {
                        mainAppNavController.navigate(Routes.SeeAllProductScreen)
                    }
                )
                1 -> WishlistScreen(
                    viewModel = viewModel,
                    onBackClick = { selectedIndex = 0 },
                    onProductClick = { productId ->
                        mainAppNavController.navigate(Routes.EachProductDetailScreen(products = productId))
                    }
                )
                2 -> CartScreen(
                    viewModel = viewModel,
                    onBackClick = { selectedIndex = 0 },
                    onCheckoutClick = { productId ->
                        mainAppNavController.navigate(Routes.CheckoutScreen(productId = productId))
                    }
                )
                3 -> ProfileScreen(
                    viewModel = viewModel,
                    onLogOutSuccess = {
                        mainAppNavController.navigate(SubNavigation.LoginSignUpScreen) {
                            popUpTo(SubNavigation.MainHomeScreen) { inclusive = true }
                        }
                    },
                    onAdminClick = {
                        mainAppNavController.navigate(Routes.AdminDashboardScreen)
                    }
                )
            }
        }
    }
}