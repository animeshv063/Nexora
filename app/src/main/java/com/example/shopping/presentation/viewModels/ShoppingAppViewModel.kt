package com.example.shopping.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.domain.useCase.AddBannerUseCase
import com.example.shopping.domain.useCase.AddCategoryUseCase
import com.example.shopping.domain.useCase.AddProductUseCase
import com.example.shopping.domain.useCase.AddtoCardUseCase
import com.example.shopping.domain.useCase.AddtoFavUseCase
import com.example.shopping.domain.useCase.CancelOrderUseCase
import com.example.shopping.domain.useCase.CreateUserUseCase
import com.example.shopping.domain.useCase.DeleteBannerUseCase
import com.example.shopping.domain.useCase.DeleteCategoryUseCase
import com.example.shopping.domain.useCase.DeleteProductUseCase
import com.example.shopping.domain.useCase.DeleteUserAccountUseCase
import com.example.shopping.domain.useCase.GetAllCategoryUseCase
import com.example.shopping.domain.useCase.GetAllFavUseCase
import com.example.shopping.domain.useCase.GetAllProductUseCase
import com.example.shopping.domain.useCase.GetAllSuggestedProductsUseCase
import com.example.shopping.domain.useCase.GetBannerUseCase
import com.example.shopping.domain.useCase.GetCartUseCase
import com.example.shopping.domain.useCase.GetCheckoutUseCase
import com.example.shopping.domain.useCase.GetSpecificCategoryitems
import com.example.shopping.domain.useCase.GetUserOrdersUseCase
import com.example.shopping.domain.useCase.GetUserUseCase
import com.example.shopping.domain.useCase.LoginUserUseCase
import com.example.shopping.domain.useCase.LoginWithGoogleUseCase
import com.example.shopping.domain.useCase.PlaceOrderUseCase
import com.example.shopping.domain.useCase.RemoveFromCartUseCase
import com.example.shopping.domain.useCase.RemoveFromFavUseCase
import com.example.shopping.domain.useCase.ResetUserOrdersUseCase
import com.example.shopping.domain.useCase.UpdateBannerUseCase
import com.example.shopping.domain.useCase.UpdateCategoryUseCase
import com.example.shopping.domain.useCase.UpdateProductUseCase
import com.example.shopping.domain.useCase.UpdateUserDataUseCase
import com.example.shopping.domain.useCase.getCategoryInLimit
import com.example.shopping.domain.useCase.getProductByID
import com.example.shopping.domain.useCase.getProductsInLimitUseCase
import com.example.shopping.domain.useCase.userProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResponseState<T>(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: T? = null
)

@HiltViewModel
class ShoppingAppViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    internal val updateUserDataUseCase: UpdateUserDataUseCase,
    internal val userProfileImageUseCase: userProfileImageUseCase,
    internal val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    internal val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getCategoryInLimitUseCase: getCategoryInLimit,
    private val getProductsInLimitUseCase: getProductsInLimitUseCase,
    private val getAllProductUseCase: GetAllProductUseCase,
    private val getProductByIDUseCase: getProductByID,
    private val addtoCardUseCase: AddtoCardUseCase,
    private val addtoFavUseCase: AddtoFavUseCase,
    private val getAllFavUseCase: GetAllFavUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val getAllCategoryUseCase: GetAllCategoryUseCase,
    private val getCheckoutUseCase: GetCheckoutUseCase,
    private val getBannerUseCase: GetBannerUseCase,
    private val getSpecificCategoryItemsUseCase: GetSpecificCategoryitems,
    private val getAllSuggestedProductsUseCase: GetAllSuggestedProductsUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val removeFromFavUseCase: RemoveFromFavUseCase,
    internal val placeOrderUseCase: PlaceOrderUseCase,
    internal val cancelOrderUseCase: CancelOrderUseCase,
    internal val getUserOrdersUseCase: GetUserOrdersUseCase,
    internal val resetUserOrdersUseCase: ResetUserOrdersUseCase,
    internal val addProductUseCase: AddProductUseCase,
    internal val updateProductUseCase: UpdateProductUseCase,
    internal val deleteProductUseCase: DeleteProductUseCase,
    internal val addCategoryUseCase: AddCategoryUseCase,
    internal val updateCategoryUseCase: UpdateCategoryUseCase,
    internal val deleteCategoryUseCase: DeleteCategoryUseCase,
    internal val addBannerUseCase: AddBannerUseCase,
    internal val updateBannerUseCase: UpdateBannerUseCase,
    internal val deleteBannerUseCase: DeleteBannerUseCase
) : ViewModel() {

    // Auth States
    private val _signUpState = MutableStateFlow(ResponseState<String>())
    val signUpState = _signUpState.asStateFlow()

    private val _loginState = MutableStateFlow(ResponseState<String>())
    val loginState = _loginState.asStateFlow()

    // User Profile
    internal val _userProfileState = MutableStateFlow(ResponseState<UserDataParent>())
    val userProfileState = _userProfileState.asStateFlow()

    internal val _profileUpdateState = MutableStateFlow(ResponseState<String>())
    val profileUpdateState = _profileUpdateState.asStateFlow()

    // Banners & Categories
    private val _bannerState = MutableStateFlow(ResponseState<List<BannerDataModels>>())
    val bannerState = _bannerState.asStateFlow()

    private val _categoriesState = MutableStateFlow(ResponseState<List<CategoryDataModels>>())
    val categoriesState = _categoriesState.asStateFlow()

    // Products
    private val _productsState = MutableStateFlow(ResponseState<List<ProductDataModels>>())
    val productsState = _productsState.asStateFlow()

    private val _productDetailState = MutableStateFlow(ResponseState<ProductDataModels>())
    val productDetailState = _productDetailState.asStateFlow()

    private val _categoryProductsState = MutableStateFlow(ResponseState<List<ProductDataModels>>())
    val categoryProductsState = _categoryProductsState.asStateFlow()

    // Cart & Wishlist
    private val _cartState = MutableStateFlow(ResponseState<List<CartDataModels>>())
    val cartState = _cartState.asStateFlow()

    private val _addToCartState = MutableStateFlow(ResponseState<String>())
    val addToCartState = _addToCartState.asStateFlow()

    private val _wishlistState = MutableStateFlow(ResponseState<List<ProductDataModels>>())
    val wishlistState = _wishlistState.asStateFlow()

    private val _addToFavState = MutableStateFlow(ResponseState<String>())
    val addToFavState = _addToFavState.asStateFlow()

    // Orders & Admin Action States
    internal val _placeOrderState = MutableStateFlow(ResponseState<String>())
    val placeOrderState = _placeOrderState.asStateFlow()

    internal val _userOrdersState = MutableStateFlow(ResponseState<List<com.example.shopping.domain.models.OrderDataModel>>())
    val userOrdersState = _userOrdersState.asStateFlow()

    internal val _cancelOrderState = MutableStateFlow(ResponseState<String>())
    val cancelOrderState = _cancelOrderState.asStateFlow()

    internal val _adminActionState = MutableStateFlow(ResponseState<String>())
    val adminActionState = _adminActionState.asStateFlow()

    // Delete Account & Google Sign In States
    internal val _deleteAccountState = MutableStateFlow(ResponseState<String>())
    val deleteAccountState = _deleteAccountState.asStateFlow()

    internal val _googleSignInState = MutableStateFlow(ResponseState<String>())
    val googleSignInState = _googleSignInState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        fetchBanners()
        fetchCategories()
        fetchProducts()
    }

    fun signUp(userData: UserData) {
        viewModelScope.launch {
            createUserUseCase.createUser(userData).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _signUpState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _signUpState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _signUpState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun resetSignUpState() {
        _signUpState.value = ResponseState()
    }

    fun login(userData: UserData) {
        viewModelScope.launch {
            loginUserUseCase.loginUser(userData).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _loginState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _loginState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _loginState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = ResponseState()
    }

    fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            getUserUseCase.getUserById(uid).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _userProfileState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _userProfileState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _userProfileState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun fetchBanners() {
        viewModelScope.launch {
            getBannerUseCase.getBannerUseCase().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _bannerState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _bannerState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _bannerState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            getAllCategoryUseCase.getAllCategoriesUseCase().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _categoriesState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _categoriesState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _categoriesState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            getAllProductUseCase.getAllProduct().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _productsState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _productsState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _productsState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun fetchProductById(productId: String) {
        viewModelScope.launch {
            getProductByIDUseCase.getProductById(productId).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _productDetailState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _productDetailState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _productDetailState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun fetchProductsByCategory(categoryName: String) {
        viewModelScope.launch {
            getSpecificCategoryItemsUseCase.getSpecificCategoryItems(categoryName).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _categoryProductsState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _categoryProductsState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _categoryProductsState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun addToCart(cartItem: CartDataModels) {
        viewModelScope.launch {
            addtoCardUseCase.addtoCart(cartItem).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _addToCartState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> {
                        _addToCartState.value = ResponseState(data = result.data)
                        fetchCart()
                    }
                    is ResultState.Error -> _addToCartState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun fetchCart() {
        viewModelScope.launch {
            getCartUseCase.getCartUseCase().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _cartState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _cartState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _cartState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun toggleFavorite(product: ProductDataModels) {
        val currentFavs = _wishlistState.value.data?.toMutableList() ?: mutableListOf()
        val isAlreadyFav = currentFavs.any { it.productId == product.productId }

        if (isAlreadyFav) {
            _wishlistState.value = ResponseState(data = currentFavs.filter { it.productId != product.productId })
            viewModelScope.launch {
                removeFromFavUseCase.removeFromFav(product.productId).collectLatest { result ->
                    when (result) {
                        is ResultState.Success -> {
                            _addToFavState.value = ResponseState(data = "Removed from Favorites")
                            fetchWishlist()
                            loadHomeData()
                            fetchProductById(product.productId)
                        }
                        is ResultState.Error -> {
                            _addToFavState.value = ResponseState(errorMessage = result.message)
                            fetchWishlist()
                        }
                        else -> {}
                    }
                }
            }
        } else {
            val updated = currentFavs + product
            _wishlistState.value = ResponseState(data = updated)
            viewModelScope.launch {
                addtoFavUseCase.addtoFav(product).collectLatest { result ->
                    when (result) {
                        is ResultState.Success -> {
                            _addToFavState.value = ResponseState(data = result.data)
                            fetchWishlist()
                            loadHomeData()
                            fetchProductById(product.productId)
                        }
                        is ResultState.Error -> {
                            _addToFavState.value = ResponseState(errorMessage = result.message)
                            fetchWishlist()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun fetchWishlist() {
        viewModelScope.launch {
            getAllFavUseCase.getAllFav().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _wishlistState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> _wishlistState.value = ResponseState(data = result.data)
                    is ResultState.Error -> _wishlistState.value = ResponseState(errorMessage = result.message)
                }
            }
        }
    }

    fun removeFromCart(cartId: String) {
        viewModelScope.launch {
            removeFromCartUseCase.removeFromCart(cartId).collectLatest { result ->
                if (result is ResultState.Success) {
                    fetchCart()
                }
            }
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            removeFromFavUseCase.removeFromFav(productId).collectLatest { result ->
                if (result is ResultState.Success) {
                    fetchWishlist()
                }
            }
        }
    }


    fun resetAddToCartState() {
        _addToCartState.value = ResponseState()
    }

    fun resetAddToFavState() {
        _addToFavState.value = ResponseState()
    }
}
