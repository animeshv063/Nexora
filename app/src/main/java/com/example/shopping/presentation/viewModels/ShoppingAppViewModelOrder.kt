package com.example.shopping.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.example.shopping.common.ResultState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun ShoppingAppViewModel.placeOrder(
    productId: String,
    quantity: Int,
    address: String,
    paymentMethod: String,
    onSuccess: () -> Unit = {}
) {
    viewModelScope.launch {
        placeOrderUseCase.placeOrder(productId, quantity, address, paymentMethod).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _placeOrderState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _placeOrderState.value = ResponseState(data = result.data)
                    loadHomeData()
                    fetchProductById(productId)
                    onSuccess()
                }
                is ResultState.Error -> _placeOrderState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.fetchUserOrders() {
    viewModelScope.launch {
        getUserOrdersUseCase.getUserOrders().collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _userOrdersState.value = ResponseState(isLoading = true)
                is ResultState.Success -> _userOrdersState.value = ResponseState(data = result.data)
                is ResultState.Error -> _userOrdersState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.cancelOrder(orderId: String, productId: String, quantity: Int, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
        cancelOrderUseCase.cancelOrder(orderId, productId, quantity).collectLatest { result ->
            when (result) {
                is ResultState.Loading -> _cancelOrderState.value = ResponseState(isLoading = true)
                is ResultState.Success -> {
                    _cancelOrderState.value = ResponseState(data = result.data)
                    fetchUserOrders()
                    loadHomeData()
                    if (productId.isNotBlank()) fetchProductById(productId)
                    onSuccess()
                }
                is ResultState.Error -> _cancelOrderState.value = ResponseState(errorMessage = result.message)
            }
        }
    }
}

fun ShoppingAppViewModel.resetOrderHistory(onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
        resetUserOrdersUseCase.resetUserOrders().collectLatest { result ->
            if (result is ResultState.Success) {
                fetchUserOrders()
                onSuccess()
            }
        }
    }
}
