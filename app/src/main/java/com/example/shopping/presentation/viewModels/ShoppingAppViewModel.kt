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
import com.example.shopping.domain.useCase.DeleteUserAccountUseCase
import com.example.shopping.domain.useCase.LoginWithGoogleUseCase
import com.example.shopping.domain.useCase.AddtoCardUseCase
import com.example.shopping.domain.useCase.AddtoFavUseCase
import com.example.shopping.domain.useCase.CreateUserUseCase
import com.example.shopping.domain.useCase.GetAllCategoryUseCase
import com.example.shopping.domain.useCase.GetAllFavUseCase
import com.example.shopping.domain.useCase.GetAllProductUseCase
import com.example.shopping.domain.useCase.GetAllSuggestedProductsUseCase
import com.example.shopping.domain.useCase.GetBannerUseCase
import com.example.shopping.domain.useCase.GetCartUseCase
import com.example.shopping.domain.useCase.GetCheckoutUseCase
import com.example.shopping.domain.useCase.GetSpecificCategoryitems
import com.example.shopping.domain.useCase.GetUserUseCase
import com.example.shopping.domain.useCase.LoginUserUseCase
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
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val userProfileImageUseCase: userProfileImageUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
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
    private val removeFromCartUseCase: com.example.shopping.domain.useCase.RemoveFromCartUseCase,
    private val removeFromFavUseCase: com.example.shopping.domain.useCase.RemoveFromFavUseCase
) : ViewModel() {



    // Auth States
    private val _signUpState = MutableStateFlow(ResponseState<String>())
    val signUpState = _signUpState.asStateFlow()

    private val _loginState = MutableStateFlow(ResponseState<String>())
    val loginState = _loginState.asStateFlow()

    // User Profile
    private val _userProfileState = MutableStateFlow(ResponseState<UserDataParent>())
    val userProfileState = _userProfileState.asStateFlow()

    private val _profileUpdateState = MutableStateFlow(ResponseState<String>())
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
        viewModelScope.launch {
            addtoFavUseCase.addtoFav(product).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> _addToFavState.value = ResponseState(isLoading = true)
                    is ResultState.Success -> {
                        _addToFavState.value = ResponseState(data = result.data)
                        fetchWishlist()
                    }
                    is ResultState.Error -> _addToFavState.value = ResponseState(errorMessage = result.message)
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



    // Delete Account State
    private val _deleteAccountState = MutableStateFlow(ResponseState<String>())
    val deleteAccountState = _deleteAccountState.asStateFlow()

    // Google Sign In State
    private val _googleSignInState = MutableStateFlow(ResponseState<String>())
    val googleSignInState = _googleSignInState.asStateFlow()

    fun loginWithGoogleToken(idToken: String) {
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

    fun deleteAccount(uid: String, password: String = "") {
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


    fun updateProfile(userDataParent: UserDataParent) {
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

    fun uploadProfileImage(uri: android.net.Uri, uid: String, currentData: UserData) {
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


    fun resetDeleteAccountState() {
        _deleteAccountState.value = ResponseState()
    }

    fun resetGoogleSignInState() {
        _googleSignInState.value = ResponseState()
    }

    fun resetProfileUpdateState() {
        _profileUpdateState.value = ResponseState()
    }

    fun resetAddToCartState() {
        _addToCartState.value = ResponseState()
    }

    fun resetAddToFavState() {
        _addToFavState.value = ResponseState()
    }
}

