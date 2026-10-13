package com.example.shopping.data.repo

import com.example.shopping.common.ResultState
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun isOwnerAuthorized(firebaseAuth: FirebaseAuth): Boolean {
    val user = firebaseAuth.currentUser ?: return true
    val currentEmail = user.email?.trim() ?: ""
    val isGoogleProvider = user.providerData.any { it.providerId == "google.com" }
    return currentEmail.equals("animeshv063@gmail.com", ignoreCase = true) && isGoogleProvider
}

internal fun addProductImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    product: ProductDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can add products."))
        close()
        return@callbackFlow
    }
    val docRef = if (product.productId.isNotBlank()) {
        firebaseFirestore.collection("Products").document(product.productId)
    } else {
        firebaseFirestore.collection("Products").document()
    }
    val productMap = hashMapOf(
        "name" to product.name,
        "description" to product.description,
        "price" to product.price,
        "finalPrice" to product.finalPrice.ifEmpty { product.price },
        "category" to product.category,
        "gender" to product.gender.ifEmpty { "Men" },
        "image" to product.image,
        "date" to (if (product.date > 0) product.date else System.currentTimeMillis()),
        "createBy" to product.createBy.ifEmpty { "Animesh" },
        "availableUnits" to product.availableUnits,
        "initialUnits" to product.initialUnits
    )
    docRef.set(productMap).addOnSuccessListener {
        trySend(ResultState.Success("Product Added to Firestore Successfully! 🚀"))
    }.addOnFailureListener { error ->
        trySend(ResultState.Error(error.localizedMessage ?: "Failed to add product"))
    }
    awaitClose {
        close()
    }
}

internal fun updateProductImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    product: ProductDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can update products."))
        close()
        return@callbackFlow
    }
    if (product.productId.isBlank()) {
        trySend(ResultState.Error("Product ID is required"))
    } else {
        val docRef = firebaseFirestore.collection("Products").document(product.productId)
        val productMap = hashMapOf(
            "name" to product.name,
            "description" to product.description,
            "price" to product.price,
            "finalPrice" to product.finalPrice.ifEmpty { product.price },
            "category" to product.category,
            "gender" to product.gender.ifEmpty { "Men" },
            "image" to product.image,
            "date" to (if (product.date > 0) product.date else System.currentTimeMillis()),
            "createBy" to product.createBy.ifEmpty { "Animesh" },
            "availableUnits" to product.availableUnits,
            "initialUnits" to product.initialUnits
        )
        docRef.set(productMap).addOnSuccessListener {
            trySend(ResultState.Success("Product Updated Successfully! 🔄"))
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to update product"))
        }
    }
    awaitClose {
        close()
    }
}

internal fun deleteProductImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    productId: String
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can delete products."))
        close()
        return@callbackFlow
    }
    if (productId.isBlank()) {
        trySend(ResultState.Error("Product ID is required"))
    } else {
        firebaseFirestore.collection("Products").document(productId).delete().addOnSuccessListener {
            trySend(ResultState.Success("Product Deleted from Firestore! 🗑️"))
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to delete product"))
        }
    }
    awaitClose {
        close()
    }
}

// Category CRUD Operations
internal fun addCategoryImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    category: CategoryDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can add categories."))
        close()
        return@callbackFlow
    }
    val trimmedName = category.name.trim()
    if (trimmedName.isEmpty()) {
        trySend(ResultState.Error("Category name cannot be empty."))
        close()
        return@callbackFlow
    }

    // Check if a category with the same name already exists (case-insensitive)
    firebaseFirestore.collection("categories").get().addOnSuccessListener { snapshot ->
        val duplicate = snapshot.documents.any { doc ->
            val existingName = doc.getString("name")?.trim().orEmpty()
            existingName.equals(trimmedName, ignoreCase = true)
        }
        if (duplicate) {
            trySend(ResultState.Error("A category named '$trimmedName' already exists!"))
            close()
            return@addOnSuccessListener
        }

        val docRef = if (category.categoryId.isNotBlank()) {
            firebaseFirestore.collection("categories").document(category.categoryId)
        } else {
            firebaseFirestore.collection("categories").document()
        }
        val categoryMap = hashMapOf(
            "name" to trimmedName,
            "categoryImage" to category.categoryImage,
            "createBy" to category.createBy.ifEmpty { "Animesh" },
            "date" to (if (category.date > 0) category.date else System.currentTimeMillis())
        )
        docRef.set(categoryMap).addOnSuccessListener {
            trySend(ResultState.Success("Category Added Successfully! 📁"))
            close()
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to add category"))
            close()
        }
    }.addOnFailureListener { error ->
        trySend(ResultState.Error(error.localizedMessage ?: "Failed to check existing categories"))
        close()
    }
    awaitClose {
        close()
    }
}

internal fun updateCategoryImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    categoryId: String,
    category: CategoryDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can update categories."))
        close()
        return@callbackFlow
    }
    if (categoryId.isBlank()) {
        trySend(ResultState.Error("Category ID is required"))
    } else {
        val docRef = firebaseFirestore.collection("categories").document(categoryId)
        val categoryMap = hashMapOf(
            "name" to category.name,
            "categoryImage" to category.categoryImage,
            "createBy" to category.createBy.ifEmpty { "Animesh" },
            "date" to (if (category.date > 0) category.date else System.currentTimeMillis())
        )
        docRef.set(categoryMap).addOnSuccessListener {
            trySend(ResultState.Success("Category Updated Successfully! 🔄"))
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to update category"))
        }
    }
    awaitClose {
        close()
    }
}

internal fun deleteCategoryImpl(
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    trySend(ResultState.Error("Category deletion is disabled."))
    awaitClose {
        close()
    }
}

// Banner CRUD Operations
internal fun addBannerImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    banner: BannerDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can add banners."))
        close()
        return@callbackFlow
    }
    val docRef = if (banner.bannerId.isNotBlank()) {
        firebaseFirestore.collection("banner").document(banner.bannerId)
    } else {
        firebaseFirestore.collection("banner").document()
    }
    val bannerMap = hashMapOf(
        "name" to banner.name,
        "image" to banner.image,
        "date" to (if (banner.date > 0) banner.date else System.currentTimeMillis())
    )
    docRef.set(bannerMap).addOnSuccessListener {
        trySend(ResultState.Success("Banner Added Successfully! 🖼️"))
    }.addOnFailureListener { error ->
        trySend(ResultState.Error(error.localizedMessage ?: "Failed to add banner"))
    }
    awaitClose {
        close()
    }
}

internal fun updateBannerImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    bannerId: String,
    banner: BannerDataModels
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can update banners."))
        close()
        return@callbackFlow
    }
    if (bannerId.isBlank()) {
        trySend(ResultState.Error("Banner ID is required"))
    } else {
        val docRef = firebaseFirestore.collection("banner").document(bannerId)
        val bannerMap = hashMapOf(
            "name" to banner.name,
            "image" to banner.image,
            "date" to (if (banner.date > 0) banner.date else System.currentTimeMillis())
        )
        docRef.set(bannerMap).addOnSuccessListener {
            trySend(ResultState.Success("Banner Updated Successfully! 🔄"))
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to update banner"))
        }
    }
    awaitClose {
        close()
    }
}

internal fun deleteBannerImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    bannerId: String
): Flow<ResultState<String>> = callbackFlow {
    trySend(ResultState.Loading)
    if (!isOwnerAuthorized(firebaseAuth)) {
        trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can delete banners."))
        close()
        return@callbackFlow
    }
    if (bannerId.isBlank()) {
        trySend(ResultState.Error("Banner ID is required"))
    } else {
        firebaseFirestore.collection("banner").document(bannerId).delete().addOnSuccessListener {
            trySend(ResultState.Success("Banner Deleted Successfully! 🗑️"))
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to delete banner"))
        }
    }
    awaitClose {
        close()
    }
}
