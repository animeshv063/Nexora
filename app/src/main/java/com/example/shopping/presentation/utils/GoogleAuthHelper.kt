package com.example.shopping.presentation.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.MessageDigest
import java.util.UUID

object GoogleAuthHelper {

    // Web Client ID from Firebase OAuth configuration (Client Type 3)
    private const val WEB_CLIENT_ID = "839246785201-7c0a6bdqgt44fobjp0jb0hiho814r611.apps.googleusercontent.com"

    suspend fun initiateGoogleSignIn(

        context: Context,
        serverClientId: String = WEB_CLIENT_ID,
        onSuccess: (idToken: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = result.credential
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                onSuccess(googleIdTokenCredential.idToken)
            } else {
                onError("Received unsupported credential type")
            }
        } catch (e: GetCredentialCancellationException) {
            // User cancelled the dialog
        } catch (e: GetCredentialException) {
            onError(e.localizedMessage ?: "Google Sign-In failed")
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}
