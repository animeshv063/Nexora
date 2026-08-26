package com.example.shopping.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderDataModel(
    var orderId: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 1,
    val address: String = "",
    val paymentMethod: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "Processing"
)
