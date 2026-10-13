package com.example.shopping.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun ShoppingAppViewModel.addProduct(product: ProductDataModels, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        addProductUseCase.addProduct(product).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchProducts()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.updateProduct(product: ProductDataModels, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        updateProductUseCase.updateProduct(product).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchProducts()
                    if (product.productId.isNotBlank()) fetchProductById(product.productId)
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.deleteProduct(productId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        deleteProductUseCase.deleteProduct(productId).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchProducts()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.addCategory(category: CategoryDataModels, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        addCategoryUseCase.addCategory(category).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchCategories()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.updateCategory(categoryId: String, category: CategoryDataModels, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        updateCategoryUseCase.updateCategory(categoryId, category).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchCategories()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.deleteCategory(categoryId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        deleteCategoryUseCase.deleteCategory(categoryId).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchCategories()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.addBanner(banner: BannerDataModels, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        addBannerUseCase.addBanner(banner).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchBanners()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.updateBanner(bannerId: String, banner: BannerDataModels, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        updateBannerUseCase.updateBanner(bannerId, banner).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchBanners()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}

fun ShoppingAppViewModel.deleteBanner(bannerId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
    viewModelScope.launch {
        deleteBannerUseCase.deleteBanner(bannerId).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _adminActionState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _adminActionState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchBanners()
                    onSuccess()
                }
                is ResultState.Error -> {
                    _adminActionState.value = ResponseState(errorMessage = result.message)
                    onError(result.message)
                }
            }
        }
    }
}
