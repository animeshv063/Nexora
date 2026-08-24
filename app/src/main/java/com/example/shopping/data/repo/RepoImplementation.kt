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
        firebaseFirestore.collection(USER_COLLECTION).document(userDataParent.nodeId).update(userDataParent.userData.toMap()).addOnCompleteListener{
            if(it.isSuccessful){
                trySend(ResultState.Success("User Updated Successfully"))
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

    override fun userProfileImage(uri : Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseStorage.getInstance().reference.child("userProfileImage/${System.currentTimeMillis()} + ${firebaseAuth.currentUser?.uid}")
            .putFile(uri?: Uri.EMPTY).addOnCompleteListener{
                it.result.storage.downloadUrl.addOnSuccessListener {imageUri ->
                    trySend(ResultState.Success(imageUri.toString()))
                }
                if (it.exception != null) {
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        awaitClose{
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
        firebaseFirestore.collection(ADD_TO_CART).document(firebaseAuth.currentUser!!.uid).collection("User_Cart").add(cartDataModels).addOnSuccessListener {
            trySend(ResultState.Success("Product Added to Cart"))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }

        awaitClose{
            close()
        }
    }

    override fun addToFav(productDataModels: ProductDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid).collection("User_Fav")
            .add(productDataModels).addOnSuccessListener {
                trySend(ResultState.Success("Product Added To Fav"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose{
            close()
        }
    }

    override fun getAllFav(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid).collection("User_Fav").get().addOnSuccessListener {
            val fav = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)
                }
                trySend(ResultState.Success(fav))
            }.addOnFailureListener{
                trySend(ResultState.Error(it.toString()))
        }

        awaitClose{
            close()
        }

    }

    override fun getCart(): Flow<ResultState<List<CartDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADD_TO_CART).document(firebaseAuth.currentUser!!.uid).collection("User_Cart").get().addOnSuccessListener {
            val cart = it.documents.mapNotNull {
                document -> document.toObject(CartDataModels::class.java)?.apply {
                    cartId = document.id
            }
            }

            trySend(ResultState.Success(cart))
        }
        awaitClose{
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

    override fun getAllSuggestedProducts(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADDTOFAV).document(firebaseAuth.currentUser!!.uid).collection("User_Fav").get().addOnSuccessListener {
            val fav = it.documents.mapNotNull { document ->

                document.toObject(ProductDataModels::class.java)

            }
            trySend(ResultState.Success(fav))
        }.addOnFailureListener{
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }

}

