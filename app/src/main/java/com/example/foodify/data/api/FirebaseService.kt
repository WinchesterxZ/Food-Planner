package com.example.foodify.data.api

import com.example.foodify.data.model.Result
import com.example.foodify.data.model.User
import com.google.firebase.auth.AuthCredential

interface FirebaseService {
    suspend fun signUpWithEmail(email: String, password: String,displayName:String): Result<User>
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signInWithGoogle(credential: AuthCredential): Result<User>
    fun logout()
    fun getCurrentUser(): User?
}