package com.example.shopping.data.repo

import com.example.shopping.common.ADDTOFAV
import com.example.shopping.common.ADD_TO_CART
import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun addToCartImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    cartDataModels: CartDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null) {
        val docRef = firebaseFirestore.collection(ADD_TO_CART).document(uid).collection("User_Cart").document()
        val itemWithId = cartDataModels.copy(cartId = docRef.id)
        docRef.set(itemWithId).addOnSuccessListener {
            trySend(ResultState.Success("Product Added to Cart"))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
    } else {
        trySend(ResultState.Error("Please log in to add items to cart"))
    }

    awaitClose {
        close()
    }
}

internal fun removeFromCartImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    cartId: String
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null && cartId.isNotBlank()) {
        firebaseFirestore.collection(ADD_TO_CART).document(uid).collection("User_Cart").document(cartId).delete().addOnSuccessListener {
            trySend(ResultState.Success("Item removed from cart"))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
    } else {
        trySend(ResultState.Error("Failed to remove item"))
    }
    awaitClose {
        close()
    }
}

internal fun addToFavImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    productDataModels: ProductDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null) {
        val docRef = if (productDataModels.productId.isNotBlank()) {
            firebaseFirestore.collection(ADDTOFAV).document(uid).collection("User_Fav").document(productDataModels.productId)
        } else {
            firebaseFirestore.collection(ADDTOFAV).document(uid).collection("User_Fav").document()
        }
        docRef.set(productDataModels).addOnSuccessListener {
            // Reserve: Decrease available units by 1 in Products collection
            if (productDataModels.productId.isNotBlank()) {
                val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
                val productRef = firebaseFirestore.collection("Products").document(productDataModels.productId)
                firebaseFirestore.runTransaction { transaction ->
                    val snapshot = transaction.get(productRef)
                    val currentUnits = snapshot.getLong("availableUnits")?.toInt() ?: 0
                    val initialUnits = snapshot.getLong("initialUnits")?.toInt() ?: (currentUnits + 1)
                    if (currentUnits > 0) {
                        transaction.update(
                            productRef,
                            mapOf(
                                "availableUnits" to currentUnits - 1,
                                "initialUnits" to initialUnits,
                                "lastResetDay" to currentDay
                            )
                        )
                    }
                }
            }
            trySend(ResultState.Success("Added to Favorites"))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
    } else {
        trySend(ResultState.Error("Please log in to favorite items"))
    }
    awaitClose {
        close()
    }
}

internal fun removeFromFavImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    productId: String
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null && productId.isNotBlank()) {
        firebaseFirestore.collection(ADDTOFAV).document(uid).collection("User_Fav").document(productId).delete().addOnSuccessListener {
            // Restore available units by 1 in Products collection
            val productRef = firebaseFirestore.collection("Products").document(productId)
            firebaseFirestore.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                val currentUnits = snapshot.getLong("availableUnits")?.toInt() ?: 0
                transaction.update(productRef, "availableUnits", currentUnits + 1)
            }
            trySend(ResultState.Success("Removed from Favorites"))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
    } else {
        trySend(ResultState.Error("Failed to remove favorite"))
    }
    awaitClose {
        close()
    }
}

internal fun getAllFavImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null) {
        firebaseFirestore.collection(ADDTOFAV).document(uid).collection("User_Fav").get().addOnSuccessListener { snapshot ->
            val favDocIds = snapshot.documents.map { it.id }
            if (favDocIds.isEmpty()) {
                trySend(ResultState.Success(emptyList()))
                return@addOnSuccessListener
            }
            // Fetch live product documents from Products collection
            firebaseFirestore.collection("Products").get().addOnSuccessListener { prodSnapshot ->
                val productMap = prodSnapshot.documents.mapNotNull { parseProductDocument(it) }.associateBy { it.productId }
                val fullFavList = favDocIds.mapNotNull { id ->
                    productMap[id] ?: snapshot.documents.find { it.id == id }?.let { parseProductDocument(it) }
                }
                trySend(ResultState.Success(fullFavList))
            }.addOnFailureListener {
                val fallback = snapshot.documents.mapNotNull { parseProductDocument(it) }
                trySend(ResultState.Success(fallback))
            }
        }.addOnFailureListener {
            trySend(ResultState.Success(emptyList()))
        }
    } else {
        trySend(ResultState.Success(emptyList()))
    }

    awaitClose {
        close()
    }
}

internal fun getCartImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<CartDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null) {
        firebaseFirestore.collection(ADD_TO_CART).document(uid).collection("User_Cart").get().addOnSuccessListener {
            val cart = it.documents.mapNotNull { document ->
                document.toObject(CartDataModels::class.java)?.apply {
                    cartId = document.id
                }
            }
            trySend(ResultState.Success(cart))
        }.addOnFailureListener {
            trySend(ResultState.Success(emptyList()))
        }
    } else {
        trySend(ResultState.Success(emptyList()))
    }
    awaitClose {
        close()
    }
}

internal fun getAllSuggestedProductsImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    val uid = firebaseAuth.currentUser?.uid
    if (uid != null) {
        firebaseFirestore.collection(ADDTOFAV).document(uid).collection("User_Fav").get().addOnSuccessListener {
            val fav = it.documents.mapNotNull { document ->
                parseProductDocument(document)
            }
            trySend(ResultState.Success(fav))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
    } else {
        trySend(ResultState.Success(emptyList()))
    }
    awaitClose {
        close()
    }
}
