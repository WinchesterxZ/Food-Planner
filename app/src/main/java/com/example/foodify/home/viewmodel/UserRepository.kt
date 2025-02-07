package com.example.foodify.home.viewmodel

import com.example.foodify.data.api.FirebaseService
import com.example.foodify.data.model.User

class UserRepository (
    private val authApiService: FirebaseService
){
    fun getCurrentUser(): User? {
        return authApiService.getCurrentUser()
    }


}
