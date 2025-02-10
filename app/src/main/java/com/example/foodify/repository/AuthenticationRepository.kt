package com.example.foodify.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.foodify.data.api.FirebaseService
import com.example.foodify.data.db.AuthLocalDataSource
import com.example.foodify.data.model.Result
import com.example.foodify.data.model.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider

class AuthenticationRepository(
    private val authLocalDataSource: AuthLocalDataSource,
    private val authApiService: FirebaseService
) {

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User> {
        return when (val result = authApiService.signUpWithEmail(email, password, displayName)) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(Exception(getFirebaseErrorMessage(result.exception)))
            }
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return when (val result = authApiService.loginWithEmail(email, password)) {
            is Result.Success -> {
                authLocalDataSource.setLoggedIn(true)
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(Exception(getFirebaseErrorMessage(result.exception)))
            }
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return authApiService.sendPasswordResetEmail(email)
    }
    private suspend fun buildCredentialRequest(credentialManager:CredentialManager,serverClientId:String , context: Context): GetCredentialResponse {
        val result = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(false)
                    .build()
            ).build()
        return credentialManager.getCredential(
            context,result
        )
    }
    suspend fun getAuthCredential(
        credentialManager: CredentialManager,
        serverClientId: String,
        context: Context
    ): AuthCredential? {
        val credentialResponse = buildCredentialRequest(credentialManager, serverClientId, context)
        val credential = credentialResponse.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
            } catch (e: GoogleIdTokenParsingException) {
                //Log.e(TAG, "Google ID Token parsing failed: ${e.localizedMessage}")
                null
            }
        }

        //Log.e(TAG, "Credential is not from Google")
        return null
    }
    suspend fun signInWithGoogle(credential: AuthCredential): Result<User> {
        return when (val result = authApiService.signInWithGoogle(credential)) {
            is Result.Success -> {
                authLocalDataSource.setLoggedIn(true)
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(Exception(getFirebaseErrorMessage(result.exception)))
            }
            is Result.Loading -> Result.Loading
        }
    }

    fun logout() {
        authApiService.logout()
        authLocalDataSource.clearUserData()
    }

    fun getCurrentUser(): User? {
        return authApiService.getCurrentUser()
    }

    fun isUserLoggedIn(): Boolean {
        return authLocalDataSource.isLoggedIn()
    }
    fun isUserGuest():Boolean{
        return  authLocalDataSource.isGuestMode()
    }
    fun saveGuestMode(isGuest:Boolean){
        authLocalDataSource.saveGuestMode(isGuest)
    }

    private fun getFirebaseErrorMessage(exception: Exception?): String {
        if (exception is FirebaseAuthException) {
            Log.e("AuthError", "FirebaseAuthException: ${exception.errorCode} - ${exception.message}")
            return when (exception.errorCode) {
                "ERROR_INVALID_CREDENTIAL" -> "Incorrect email or password. Please try again."
                "ERROR_INVALID_EMAIL" -> "Invalid email format."
                "ERROR_WEAK_PASSWORD" -> "Password should be at least 6 characters."
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
                "ERROR_USER_DISABLED" -> "This account has been disabled."
                "ERROR_TOO_MANY_REQUESTS" -> "Too many login attempts. Try again later."
                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your internet connection."
                else -> "Authentication failed: ${exception.errorCode}"
            }
        } else {
            Log.e("AuthError", "Unknown Exception: ${exception?.message}")
            return "Email Is Not Verified"
        }
    }

}
