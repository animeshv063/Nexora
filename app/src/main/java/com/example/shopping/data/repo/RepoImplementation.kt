package com.example.shopping.data.repo

import android.net.Uri
import com.example.shopping.common.ADDTOFAV
import com.example.shopping.common.ADD_TO_CART
import com.example.shopping.common.PRODUCT_COLLECTION
import com.example.shopping.common.ResultState
import com.example.shopping.common.USER_COLLECTION
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CartDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.example.shopping.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RepoImplementation @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseFirestore: FirebaseFirestore,
    @dagger.hilt.android.qualifiers.ApplicationContext val context: android.content.Context
) : Repo
{
    override fun registerUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password).addOnCompleteListener {
            if(it.isSuccessful){
                firebaseFirestore.collection(USER_COLLECTION).document(it.result.user?.uid.toString()).set((userData)).addOnCompleteListener {
                    if(it.isSuccessful){
                        trySend(ResultState.Success("User Registered Successfully and add to Firestore"))
                    }
                    else{
                        if(it.exception != null){
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
                trySend(ResultState.Success("User Registered Successfully"))
            }else{
                if(it.exception != null){
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        }

        awaitClose{
            close()
        }
    }

    override fun loginUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        firebaseAuth.signInWithEmailAndPassword(userData.email, userData.password).addOnCompleteListener{
            if(it.isSuccessful){
                trySend(ResultState.Success("User Login Successfully"))
            }else{
                if(it.exception != null){
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        }
        awaitClose{
            close()
        }
    }

    override fun getuserById(uid: String): Flow<ResultState<UserDataParent>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(USER_COLLECTION).document(uid).get().addOnCompleteListener{ task ->
            if (task.isSuccessful && task.result != null) {
                val doc = task.result
                val data = doc.toObject(UserData::class.java) ?: UserData(
                    email = firebaseAuth.currentUser?.email ?: "",
                    firstName = firebaseAuth.currentUser?.displayName?.split(" ")?.firstOrNull() ?: "",
                    lastName = firebaseAuth.currentUser?.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""
                )
                val userDataParent = UserDataParent(doc.id, userData = data)
                trySend(ResultState.Success(userDataParent))
            } else {
                val fallbackData = UserData(
                    email = firebaseAuth.currentUser?.email ?: "",
                    firstName = firebaseAuth.currentUser?.displayName?.split(" ")?.firstOrNull() ?: "",
                    lastName = firebaseAuth.currentUser?.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""
                )
                trySend(ResultState.Success(UserDataParent(nodeId = uid, userData = fallbackData)))
            }
        }
        awaitClose{
            close()
        }
    }


    override fun updateUserData(userDataParent: UserDataParent): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userDataParent.nodeId)
            .set(userDataParent.userData, com.google.firebase.firestore.SetOptions.merge())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(ResultState.Success("User Updated Successfully"))
                } else {
                    if (it.exception != null) {
                        trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                    }
                }
            }
        awaitClose {
            close()
        }
    }


    override fun userProfileImage(uri: Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = firebaseAuth.currentUser?.uid ?: "user_${System.currentTimeMillis()}"
        val storageRef = FirebaseStorage.getInstance().reference.child("userProfileImage/${uid}_${System.currentTimeMillis()}.jpg")
        
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                trySend(ResultState.Success(downloadUri.toString()))
            }.addOnFailureListener { e ->
                trySend(ResultState.Error(e.localizedMessage ?: "Failed to get download url"))
            }
        }.addOnFailureListener { e ->
            trySend(ResultState.Error(e.localizedMessage ?: "Failed to upload image"))
        }

        awaitClose {
            close()
        }
    }


    override fun getCategoriesInLimited(): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow {
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

    private fun parseProductDocument(document: com.google.firebase.firestore.DocumentSnapshot): ProductDataModels? {
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

    override fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
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

    override fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
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

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModels>> = callbackFlow {
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

    override fun addToCart(cartDataModels: CartDataModels): Flow<ResultState<String>> = callbackFlow {
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

    override fun removeFromCart(cartId: String): Flow<ResultState<String>> = callbackFlow {
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

    override fun addToFav(productDataModels: ProductDataModels): Flow<ResultState<String>> = callbackFlow {
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

    override fun removeFromFav(productId: String): Flow<ResultState<String>> = callbackFlow {
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


    override fun getAllFav(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            firebaseFirestore.collection(ADDTOFAV).document(uid).collection("User_Fav").get().addOnSuccessListener {
                val fav = it.documents.mapNotNull { document ->
                    parseProductDocument(document)
                }
                trySend(ResultState.Success(fav))
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

    override fun getCart(): Flow<ResultState<List<CartDataModels>>> = callbackFlow {
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


    override fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow {
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

    override fun getCheckout(productid: String): Flow<ResultState<ProductDataModels>> = callbackFlow {
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

    override fun getBanner(): Flow<ResultState<List<BannerDataModels>>> = callbackFlow {
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

    override fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
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

    override fun deleteUserAccount(uid: String, password: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val user = firebaseAuth.currentUser

        if (user != null) {
            // Delete user documents from Firestore
            firebaseFirestore.collection(USER_COLLECTION).document(uid).delete()
            firebaseFirestore.collection("add_to_cart").document(uid).delete()
            firebaseFirestore.collection("add_to_fav").document(uid).delete()

            if (password.isNotBlank() && user.email != null) {
                // Reauthenticate with password
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    user.delete().addOnCompleteListener { authTask ->
                        firebaseAuth.signOut()
                        trySend(ResultState.Success("Account successfully deleted"))
                    }
                }
            } else {
                user.delete().addOnCompleteListener { authTask ->
                    firebaseAuth.signOut()
                    trySend(ResultState.Success("Account successfully deleted"))
                }
            }
        } else {
            firebaseAuth.signOut()
            trySend(ResultState.Success("Account successfully deleted"))
        }

        awaitClose {
            close()
        }
    }




    override fun loginWithGoogle(idToken: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    // Check or create Firestore entry
                    val userRef = firebaseFirestore.collection(USER_COLLECTION).document(user.uid)
                    userRef.get().addOnSuccessListener { doc ->
                        if (!doc.exists()) {
                            val names = (user.displayName ?: "User").split(" ")
                            val firstName = names.firstOrNull() ?: "User"
                            val lastName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""
                            val newUserData = UserData(
                                firstName = firstName,
                                lastName = lastName,
                                email = user.email ?: "",
                                profileImage = user.photoUrl?.toString() ?: ""
                            )
                            userRef.set(newUserData)
                        }
                        trySend(ResultState.Success("Google Sign-In Successful"))
                    }.addOnFailureListener {
                        trySend(ResultState.Success("Google Sign-In Successful"))
                    }
                } else {
                    trySend(ResultState.Success("Google Sign-In Successful"))
                }
            } else {
                trySend(ResultState.Error(task.exception?.localizedMessage ?: "Google Sign-In Failed"))
            }
        }
        awaitClose {
            close()
        }
    }

    override fun getAllSuggestedProducts(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {

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

    override fun placeOrder(
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

    override fun cancelOrder(
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

    override fun getUserOrders(): Flow<ResultState<List<com.example.shopping.domain.models.OrderDataModel>>> = callbackFlow {
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

    override fun resetUserOrders(): Flow<ResultState<String>> = callbackFlow {
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

    private fun isOwnerAuthorized(): Boolean {
        val user = firebaseAuth.currentUser ?: return true
        val currentEmail = user.email?.trim() ?: ""
        val isGoogleProvider = user.providerData.any { it.providerId == "google.com" }
        return currentEmail.equals("animeshv063@gmail.com", ignoreCase = true) && isGoogleProvider
    }

    override fun addProduct(product: ProductDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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

    override fun updateProduct(product: ProductDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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

    override fun deleteProduct(productId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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
    override fun addCategory(category: CategoryDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
            trySend(ResultState.Error("Access Denied: Only animeshv063@gmail.com can add categories."))
            close()
            return@callbackFlow
        }
        val docRef = if (category.categoryId.isNotBlank()) {
            firebaseFirestore.collection("categories").document(category.categoryId)
        } else {
            firebaseFirestore.collection("categories").document()
        }
        val categoryMap = hashMapOf(
            "name" to category.name,
            "categoryImage" to category.categoryImage,
            "createBy" to category.createBy.ifEmpty { "Animesh" },
            "date" to (if (category.date > 0) category.date else System.currentTimeMillis())
        )
        docRef.set(categoryMap).addOnSuccessListener {
            trySend(ResultState.Success("Category Added Successfully! 📁"))
        }.addOnFailureListener { error ->
            trySend(ResultState.Error(error.localizedMessage ?: "Failed to add category"))
        }
        awaitClose {
            close()
        }
    }

    override fun updateCategory(categoryId: String, category: CategoryDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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

    override fun deleteCategory(categoryId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        trySend(ResultState.Error("Category deletion is disabled."))
        awaitClose {
            close()
        }
    }

    // Banner CRUD Operations
    override fun addBanner(banner: BannerDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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

    override fun updateBanner(bannerId: String, banner: BannerDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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

    override fun deleteBanner(bannerId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (!isOwnerAuthorized()) {
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
}


