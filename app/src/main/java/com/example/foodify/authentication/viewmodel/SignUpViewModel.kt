package com.example.foodify.authentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.repository.AuthenticationRepository
import com.example.foodify.data.model.User
import kotlinx.coroutines.launch
import com.example.foodify.data.model.Result

class SignUpViewModel(
    private val authRepository: AuthenticationRepository
):ViewModel() {

    private val _signupState = MutableLiveData<Result<User>?>()
    val signupState: LiveData<Result<User>?> get() = _signupState

    fun signupWithEmail(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _signupState.value = Result.Loading // Show loading
            val result = authRepository.signUpWithEmail(email, password, displayName)
            _signupState.value = result // Update UI state
        }
    }

}