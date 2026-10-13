package com.example.shopping.data.repo

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun getCategoriesInLimitedImpl(
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("categories").limit(15).get().addOnSuccessListener { querySnapshot ->
        val categories = querySnapshot.documents.mapNotNull { document ->
            try {
                val name = document.getString("name") ?: ""
                val categoryImageRaw = document.getString("categoryImage") ?: document.getString("image") ?: document.getString("imageUrl") ?: ""
                val categoryImage = com.example.shopping.presentation.utils.sanitizeImageUrl(categoryImageRaw)
                val createBy = document.getString("createBy") ?: ""
                val dateVal = document.get("date")
                val date = when (dateVal) {
                    is Number -> dateVal.toLong()
                    is String -> dateVal.toLongOrNull() ?: System.currentTimeMillis()
                    else -> System.currentTimeMillis()
                }
                CategoryDataModels(
                    categoryId = document.id,
                    name = name,
                    categoryImage = categoryImage,
                    createBy = createBy,
                    date = date
                )
            } catch (e: Exception) {
                null
            }
        }
        trySend(ResultState.Success(categories))
    }.addOnFailureListener {
        trySend(ResultState.Success(emptyList()))
    }

    awaitClose {
        close()
    }
}

internal fun parseProductDocument(document: com.google.firebase.firestore.DocumentSnapshot): ProductDataModels? {
    return try {
        val name = document.getString("name") ?: ""
        val description = document.getString("description") ?: ""
        val priceVal = document.get("price")
        val price = when (priceVal) {
            is Number -> priceVal.toString()
            is String -> priceVal
            else -> ""
        }
        val finalPriceVal = document.get("finalPrice")
        val finalPrice = when (finalPriceVal) {
            is Number -> finalPriceVal.toString()
            is String -> finalPriceVal
            else -> price
        }.ifEmpty { price }

        val category = document.getString("category") ?: ""
        val gender = document.getString("gender") ?: "Men"
        val image = document.getString("image") ?: ""
        val createBy = document.getString("createBy") ?: ""
        val dateVal = document.get("date")
        val date = when (dateVal) {
            is Number -> dateVal.toLong()
            is String -> dateVal.toLongOrNull() ?: System.currentTimeMillis()
            else -> System.currentTimeMillis()
        }
        val unitsVal = document.get("availableUnits")
        val availableUnits = when (unitsVal) {
            is Number -> unitsVal.toInt()
            is String -> unitsVal.toIntOrNull() ?: 20
            else -> 20
        }
        val initialVal = document.get("initialUnits")
        val initialUnits = when (initialVal) {
            is Number -> initialVal.toInt()
            is String -> initialVal.toIntOrNull() ?: availableUnits
            else -> availableUnits
        }
        val resetDayVal = document.get("lastResetDay")
        val lastResetDay = when (resetDayVal) {
            is Number -> resetDayVal.toLong()
            is String -> resetDayVal.toLongOrNull() ?: 0L
            else -> 0L
        }

        // Midnight (00:00) check: Calculate current day epoch (System.currentTimeMillis() / 86400000)
        val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val finalAvailableUnits = if (lastResetDay > 0 && currentDay > lastResetDay && initialUnits > 0) {
            // Past midnight: reset units back to original
            try {
                document.reference.update(
                    mapOf(
                        "availableUnits" to initialUnits,
                        "lastResetDay" to currentDay
                    )
                )
            } catch (e: Exception) {}
            initialUnits
        } else {
            availableUnits
        }

        ProductDataModels(
            name = name,
            description = description,
            price = price,
            finalPrice = finalPrice,
            category = category,
            gender = gender,
            image = image,
            date = date,
            createBy = createBy,
            availableUnits = finalAvailableUnits,
            initialUnits = initialUnits,
            lastResetDay = lastResetDay,
            productId = document.id
        )
    } catch (e: Exception) {
        null
    }
}

internal fun getProductsInLimitedImpl(
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("Products").limit(20).get().addOnSuccessListener {
        val products = it.documents.mapNotNull { document ->
            parseProductDocument(document)
        }
        trySend(ResultState.Success(products))
    }.addOnFailureListener {
        trySend(ResultState.Success(emptyList()))
    }
    awaitClose {
        close()
    }
}

internal fun getAllProductsImpl(
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("Products").get().addOnSuccessListener {
        val products = it.documents.mapNotNull { document ->
            parseProductDocument(document)
        }
        trySend(ResultState.Success(products))
    }.addOnFailureListener {
        trySend(ResultState.Success(emptyList()))
    }
    awaitClose {
        close()
    }
}

internal fun getProductByIdImpl(
    firebaseFirestore: FirebaseFirestore,
    productId: String
): Flow<ResultState<ProductDataModels>> = callbackFlow {
    trySend(ResultState.Loading)
    if (productId.isBlank()) {
        trySend(ResultState.Error("Product ID is blank"))
        close()
        return@callbackFlow
    }
    firebaseFirestore.collection("Products").document(productId).get().addOnSuccessListener { doc ->
        val product = parseProductDocument(doc)
        if (product != null) {
            trySend(ResultState.Success(product))
        } else {
            trySend(ResultState.Error("Product not found"))
        }
    }.addOnFailureListener {
        trySend(ResultState.Error(it.localizedMessage ?: "Failed to fetch product"))
    }
    awaitClose {
        close()
    }
}

internal fun getAllCategoriesImpl(
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("categories").get().addOnSuccessListener { querySnapshot ->
        val categories = querySnapshot.documents.mapNotNull { document ->
            try {
                val name = document.getString("name") ?: ""
                val categoryImage = document.getString("categoryImage") ?: ""
                val createBy = document.getString("createBy") ?: ""
                val dateVal = document.get("date")
                val date = when (dateVal) {
                    is Number -> dateVal.toLong()
                    is String -> dateVal.toLongOrNull() ?: System.currentTimeMillis()
                    else -> System.currentTimeMillis()
                }
                CategoryDataModels(
                    categoryId = document.id,
                    name = name,
                    categoryImage = categoryImage,
                    createBy = createBy,
                    date = date
                )
            } catch (e: Exception) {
                null
            }
        }
        trySend(ResultState.Success(categories))
    }.addOnFailureListener {
        trySend(ResultState.Success(emptyList()))
    }

    awaitClose {
        close()
    }
}

internal fun getCheckoutImpl(
    firebaseFirestore: FirebaseFirestore,
    productid: String
): Flow<ResultState<ProductDataModels>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("Products").document(productid).get().addOnSuccessListener {
        val product = parseProductDocument(it) ?: ProductDataModels(productId = productid)
        trySend(ResultState.Success(product))
    }.addOnFailureListener {
        trySend(ResultState.Error(it.localizedMessage ?: "Failed to fetch checkout details"))
    }
    awaitClose {
        close()
    }
}

internal fun getBannerImpl(
    firebaseFirestore: FirebaseFirestore
): Flow<ResultState<List<BannerDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("banner").get().addOnSuccessListener { querySnapshot ->
        val banner = querySnapshot.documents.mapNotNull { document ->
            try {
                val name = document.getString("name") ?: ""
                val imageRaw = document.getString("image") ?: document.getString("bannerImage") ?: document.getString("imageUrl") ?: ""
                val image = com.example.shopping.presentation.utils.sanitizeImageUrl(imageRaw)
                val dateVal = document.get("date")
                val date = when (dateVal) {
                    is Number -> dateVal.toLong()
                    is String -> dateVal.toLongOrNull() ?: System.currentTimeMillis()
                    else -> System.currentTimeMillis()
                }
                BannerDataModels(
                    bannerId = document.id,
                    name = name,
                    image = image,
                    date = date
                )
            } catch (e: Exception) {
                null
            }
        }
        trySend(ResultState.Success(banner))
    }.addOnFailureListener {
        trySend(ResultState.Success(emptyList()))
    }
    awaitClose {
        close()
    }
}

internal fun getSpecificCategoryItemsImpl(
    firebaseFirestore: FirebaseFirestore,
    categoryName: String
): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
    trySend(ResultState.Loading)
    firebaseFirestore.collection("Products").whereEqualTo("category", categoryName).get().addOnSuccessListener {
        val product = it.documents.mapNotNull { document ->
            parseProductDocument(document)
        }
        trySend(ResultState.Success(product))
    }.addOnFailureListener {
        trySend(ResultState.Success(emptyList()))
    }
    awaitClose {
        close()
    }
}
