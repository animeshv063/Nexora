package com.example.shopping.data.repo

import android.content.Context
import com.example.shopping.common.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun placeOrderImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    context: Context,
    productId: String,
    quantity: Int,
    address: String,
    paymentMethod: String
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid ?: "default_user"
    val orderId = "order_${System.currentTimeMillis()}"

    val orderModel = com.example.shopping.domain.models.OrderDataModel(
        orderId = orderId,
        productId = productId,
        productName = "Fashion Item",
        quantity = quantity,
        address = address,
        paymentMethod = paymentMethod,
        orderDate = System.currentTimeMillis(),
        status = "Order Placed"
    )

    // 1. Immediately persist locally so My Orders NEVER misses an order
    com.example.shopping.presentation.utils.UserOrdersStorage.saveOrderLocally(context, uid, orderModel)

    // 2. Persist to Firestore
    val orderData = hashMapOf(
        "orderId" to orderId,
        "productId" to productId,
        "quantity" to quantity,
        "address" to address,
        "paymentMethod" to paymentMethod,
        "orderDate" to orderModel.orderDate,
        "status" to "Order Placed"
    )
    firebaseFirestore.collection("Orders").document(uid).collection("User_Orders").document(orderId).set(orderData)

    // 3. Safe stock decrement transaction
    if (productId.isNotBlank()) {
        val productRef = firebaseFirestore.collection("Products").document(productId)
        firebaseFirestore.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            if (snapshot.exists()) {
                val currentUnits = snapshot.getLong("availableUnits")?.toInt() ?: 20
                val newUnits = maxOf(0, currentUnits - quantity)
                transaction.update(productRef, "availableUnits", newUnits)
            }
        }
    }

    trySend(ResultState.Success("Order Placed Successfully! 🎉"))
    awaitClose {
        close()
    }
}

internal fun cancelOrderImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    context: Context,
    orderId: String,
    productId: String,
    quantity: Int
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid ?: "default_user"

    // 1. Update locally
    com.example.shopping.presentation.utils.UserOrdersStorage.updateOrderStatusLocally(context, uid, orderId, "Cancelled")

    // 2. Update Firestore
    firebaseFirestore.collection("Orders").document(uid).collection("User_Orders").document(orderId)
        .update("status", "Cancelled")

    // 3. Restore product availableUnits up to initialUnits
    if (productId.isNotBlank()) {
        val productRef = firebaseFirestore.collection("Products").document(productId)
        firebaseFirestore.runTransaction { transaction ->
            val snapshot = transaction.get(productRef)
            if (snapshot.exists()) {
                val currentUnits = snapshot.getLong("availableUnits")?.toInt() ?: 0
                val initialUnits = snapshot.getLong("initialUnits")?.toInt() ?: (currentUnits + quantity)
                val restoredUnits = minOf(initialUnits, currentUnits + quantity)
                transaction.update(productRef, "availableUnits", restoredUnits)
            }
        }
    }
    trySend(ResultState.Success("Order Cancelled & stock restored!"))
    awaitClose {
        close()
    }
}

internal fun getUserOrdersImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    context: Context
): Flow<ResultState<List<com.example.shopping.domain.models.OrderDataModel>>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid ?: "default_user"
    val localOrders = com.example.shopping.presentation.utils.UserOrdersStorage.getLocalOrders(context, uid)

    firebaseFirestore.collection("Orders").document(uid).collection("User_Orders")
        .get()
        .addOnSuccessListener { query ->
            val remoteList = query.documents.mapNotNull { doc ->
                val orderId = doc.getString("orderId") ?: doc.id
                val productId = doc.getString("productId") ?: ""
                val productName = doc.getString("productName") ?: "Fashion Product"
                val qty = (doc.getLong("quantity") ?: 1L).toInt()
                val address = doc.getString("address") ?: ""
                val payment = doc.getString("paymentMethod") ?: "COD"
                val date = doc.getLong("orderDate") ?: System.currentTimeMillis()
                val status = doc.getString("status") ?: "Processing"

                com.example.shopping.domain.models.OrderDataModel(
                    orderId = orderId,
                    productId = productId,
                    productName = productName,
                    quantity = qty,
                    address = address,
                    paymentMethod = payment,
                    orderDate = date,
                    status = status
                )
            }
            val combinedMap = (localOrders + remoteList).associateBy { it.orderId }
            trySend(ResultState.Success(combinedMap.values.sortedByDescending { it.orderDate }))
        }
        .addOnFailureListener {
            trySend(ResultState.Success(localOrders.sortedByDescending { it.orderDate }))
        }

    awaitClose {
        close()
    }
}

internal fun resetUserOrdersImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    context: Context
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid ?: "default_user"
    val localOrders = com.example.shopping.presentation.utils.UserOrdersStorage.getLocalOrders(context, uid)
    val hasActive = localOrders.any { !it.status.equals("Cancelled", ignoreCase = true) }
    if (hasActive) {
        trySend(ResultState.Error("Cannot reset order history with live orders. Please cancel active orders first!"))
        close()
        return@callbackFlow
    }

    com.example.shopping.presentation.utils.UserOrdersStorage.clearOrdersLocally(context, uid)
    firebaseFirestore.collection("Orders").document(uid).collection("User_Orders").get()
        .addOnSuccessListener { snapshot ->
            val batch = firebaseFirestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                trySend(ResultState.Success("Order history reset successfully! 🗑️"))
            }.addOnFailureListener {
                trySend(ResultState.Success("Order history cleared locally"))
            }
        }
        .addOnFailureListener {
            trySend(ResultState.Success("Order history cleared locally"))
        }
    awaitClose {
        close()
    }
}
