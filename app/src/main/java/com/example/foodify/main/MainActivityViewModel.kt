package com.example.foodify.main

import androidx.lifecycle.ViewModel
import com.example.foodify.repository.AuthenticationRepository
import com.example.foodify.data.model.User

class MainActivityViewModel(
    private val authRepository: AuthenticationRepository
):ViewModel() {


    fun logout() {
        authRepository.logout()
    }
    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()

    }
    fun isUserGuest():Boolean{
        return authRepository.isUserGuest()
    }
}