package com.example.foodify.data.api

import com.example.foodify.data.model.Result
import com.example.foodify.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

class FirebaseServiceImpl(private val auth: FirebaseAuth) : FirebaseService {

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.sendEmailVerification()?.await()
            result.user?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName).build()
            )?.await()
            User.fromFirebase(result.user)?.let { Result.Success(it) }
                ?: Result.Error(Exception("User creation failed"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user?.isEmailVerified == true) {
                User.fromFirebase(user)?.let {
                    return Result.Success(it)
                } ?: return Result.Error(Exception("User data missing"))
            } else {
                return Result.Error(Exception("Email not verified"))
            }
        } catch (e: FirebaseAuthException) {
            return Result.Error(e)
        }

    }



    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun signInWithGoogle(credential: AuthCredential): Result<User> {
        try {
            val result = auth.signInWithCredential(credential).await()
            User.fromFirebase(result.user)?.let {
                return Result.Success(it)
            } ?: return Result.Error(Exception("Google sign-in failed"))
        }catch (e: Exception) {
           return Result.Error(e)
        }

    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        return User.fromFirebase(auth.currentUser)
    }

}