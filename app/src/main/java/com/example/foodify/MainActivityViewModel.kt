package com.example.foodify

import androidx.lifecycle.ViewModel
import com.example.foodify.repository.AuthenticationRepository
import com.example.foodify.data.model.User

class MainActivityViewModel(
    private val authRepository: AuthenticationRepository
):ViewModel() {
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun logout() {
        authRepository.logout()
    }
    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()

    }
}