package com.example.foodify.data.model

import com.google.firebase.auth.FirebaseUser

data class User(
    val uid: String, // Firebase UID (Unique Identifier) - essential
    val email: String?, // Email address (nullable, as Google sign-in might not provide it)
    val displayName: String?, // User's display name (from Firebase or custom)
    val photoUrl: String?, // Photo URL (from Firebase or custom)
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,


){
    companion object {
        fun fromFirebase(firebaseUser: FirebaseUser?): User? {
            return firebaseUser?.let {
                User(
                    uid = it.uid,
                    email = it.email,
                    displayName = it.displayName,
                    photoUrl = it.photoUrl?.toString(),
                    firstName = null, // Can be set from Firestore later
                    lastName = null,
                    phoneNumber = it.phoneNumber
                )
            }
        }
    }
}