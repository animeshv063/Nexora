package com.example.shopping.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
import com.example.shopping.ui.theme.DarkInputBorder
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

@Composable
fun MainContainer(
    mainAppNavController: androidx.navigation.NavHostController,
    viewModel: ShoppingAppViewModel
) {
    val context = LocalContext.current
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    // When on Wishlist (1), Cart/Orders (2), or Profile (3):
    // Back button or back gesture returns to Home page (0)
    BackHandler(enabled = selectedIndex != 0) {
        selectedIndex = 0
    }

    // When on Home page (0):
    // Back button or back gesture prompts exit confirmation dialog
    BackHandler(enabled = selectedIndex == 0) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OrangePrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Exit",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Exit Nexora?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to close the application?",
                    fontSize = 14.sp,
                    color = TextMuted,
                    lineHeight = 20.sp
                )
            },
            containerColor = DarkCard,
            shape = RoundedCornerShape(18.dp),
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DarkInputBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkCard)
                ) {
                    Text(text = "Cancel", color = TextWhite, fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finish()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(text = "Exit", color = com.example.shopping.ui.theme.ButtonTextColor, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    val items = listOf(
        BottomBarItem("Home", Icons.Filled.Home),
        BottomBarItem("WishList", Icons.Filled.Favorite),
        BottomBarItem("Cart", Icons.Filled.ShoppingCart),
        BottomBarItem("Profile", Icons.Filled.Person)
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(
                color = DarkCard,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        color = DarkInputBorder.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items.forEachIndexed { index, item ->
                                val isSelected = selectedIndex == index
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(OrangePrimary)
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .clickable { selectedIndex = index },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = com.example.shopping.ui.theme.ButtonTextColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = item.title,
                                                color = com.example.shopping.ui.theme.ButtonTextColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { selectedIndex = index }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (index) {
                                                0 -> Icons.Outlined.Home
                                                1 -> Icons.Outlined.FavoriteBorder
                                                2 -> Icons.Outlined.ShoppingCart
                                                3 -> Icons.Outlined.Person
                                                else -> item.icon
                                            },
                                            contentDescription = item.title,
                                            tint = TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.title,
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
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
                .padding(bottom = innerPadding.calculateBottomPadding())
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
                    },
                    isStandalone = false
                )
                3 -> ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { selectedIndex = 0 },
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