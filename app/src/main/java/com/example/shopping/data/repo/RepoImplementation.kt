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

    var firebaseAuth: FirebaseAuth, var firebaseFirestore: FirebaseFirestore

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
        firebaseFirestore.collection(USER_COLLECTION).document(uid).get().addOnCompleteListener{
            if(it.isSuccessful){
                val data = it.result.toObject(UserData::class.java)!!
                val userDataParent = UserDataParent(it.result.id, userData = data)
                trySend(ResultState.Success(userDataParent))
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


        firebaseFirestore.collection("categories").limit(7).get().addOnSuccessListener {querySnapshot ->
            val categories = querySnapshot.documents.mapNotNull {
                document ->
                document.toObject(CategoryDataModels::class.java)
            }
            trySend(ResultState.Success(categories))
        }.addOnFailureListener{exception ->
            trySend(ResultState.Error(exception.toString()))
        }
        awaitClose{
            close()
        }

    }
    override fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").limit(10).get().addOnSuccessListener {
            val products = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {
                    productId = document.id
                }
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose{
            close()
        }
    }

    override fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").get().addOnSuccessListener {
            val products = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels:: class.java)?.apply {
                    productId = document.id
                }
            }

            trySend(ResultState.Success(products))

        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }

        awaitClose{
            close()
        }

    }

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModels>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(PRODUCT_COLLECTION).document(productId).get().addOnSuccessListener {
            val product = it.toObject(ProductDataModels::class.java)
            trySend(ResultState.Success(product!!))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose{
            close()

        }
    }

    override fun addToCart(cartDataModels: CartDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            firebaseFirestore.collection(ADD_TO_CART).document(uid).collection("User_Cart").add(cartDataModels).addOnSuccessListener {
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
                    document.toObject(ProductDataModels::class.java)
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
        firebaseFirestore.collection("categories").get().addOnSuccessListener {
            val categories = it.documents.mapNotNull { document ->
                document.toObject(CategoryDataModels::class.java)
            }
            trySend(ResultState.Success(categories))

        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))

        }

        awaitClose {
            close()
        }
    }

    override fun getCheckout(productid: String): Flow<ResultState<ProductDataModels>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").document(productid).get().addOnSuccessListener {
            val product = it.toObject(ProductDataModels::class.java)
            trySend(ResultState.Success(product!!))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose{
            close()
        }
    }

    override fun getBanner(): Flow<ResultState<List<BannerDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("banner").get().addOnSuccessListener {
            val banner = it.documents.mapNotNull { document ->
                document.toObject(BannerDataModels::class.java)
            }
            trySend(ResultState.Success(banner))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose{
            close()
        }
    }

    override fun getSpecificCategoryItems(categoryName: String) : Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").whereEqualTo("category", categoryName).get().addOnSuccessListener {
            val product = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {

                    productId = document.id
                }
            }
            trySend(ResultState.Success(product))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose{
            close()
        }
    }

    override fun deleteUserAccount(uid: String, password: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val user = firebaseAuth.currentUser

        if (user != null) {
            val performDelete = {
                // Delete user's document in Firestore
                firebaseFirestore.collection(USER_COLLECTION).document(uid).delete().addOnCompleteListener {
                    // Delete user from Firebase Auth
                    user.delete().addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            firebaseAuth.signOut()
                            trySend(ResultState.Success("Account permanently deleted"))
                        } else {
                            val err = authTask.exception?.localizedMessage ?: "Failed to delete user"
                            trySend(ResultState.Error(err))
                        }
                    }
                }
            }

            if (password.isNotBlank() && user.email != null) {
                // Reauthenticate with user's password first so Firebase allows deletion without errors
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        performDelete()
                    } else {
                        trySend(ResultState.Error(reauthTask.exception?.localizedMessage ?: "Invalid password for account deletion"))
                    }
                }
            } else {
                user.delete().addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        firebaseFirestore.collection(USER_COLLECTION).document(uid).delete()
                        firebaseAuth.signOut()
                        trySend(ResultState.Success("Account permanently deleted"))
                    } else {
                        val errorMsg = authTask.exception?.localizedMessage ?: ""
                        if (errorMsg.contains("recent", ignoreCase = true)) {
                            // If security token requires reauth, try to delete firestore first and prompt for password
                            trySend(ResultState.Error("REAUTH_NEEDED"))
                        } else {
                            trySend(ResultState.Error(errorMsg))
                        }
                    }
                }
            }
        } else {
            trySend(ResultState.Error("No active user logged in"))
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
                    document.toObject(ProductDataModels::class.java)
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
}


