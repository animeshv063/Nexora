package com.example.shopping.data.repo

import android.net.Uri
import com.example.shopping.common.ResultState
import com.example.shopping.common.USER_COLLECTION
import com.example.shopping.domain.models.UserData
import com.example.shopping.domain.models.UserDataParent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun registerUserWithEmailAndPasswordImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    userData: UserData
): Flow<ResultState<String>> = callbackFlow {
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

internal fun loginUserWithEmailAndPasswordImpl(
    firebaseAuth: FirebaseAuth,
    userData: UserData
): Flow<ResultState<String>> = callbackFlow {

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

internal fun getuserByIdImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    uid: String
): Flow<ResultState<UserDataParent>> = callbackFlow {
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


internal fun updateUserDataImpl(
    firebaseFirestore: FirebaseFirestore,
    userDataParent: UserDataParent
): Flow<ResultState<String>> = callbackFlow {
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


internal fun userProfileImageImpl(
    firebaseAuth: FirebaseAuth,
    uri: Uri
): Flow<ResultState<String>> = callbackFlow {
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

internal fun deleteUserAccountImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    uid: String,
    password: String
): Flow<ResultState<String>> = callbackFlow {
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


internal fun loginWithGoogleImpl(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    idToken: String
): Flow<ResultState<String>> = callbackFlow {
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
