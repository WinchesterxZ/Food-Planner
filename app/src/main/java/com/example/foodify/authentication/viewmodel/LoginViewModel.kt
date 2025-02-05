package com.example.foodify.authentication.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.repository.AuthenticationRepository
import com.example.foodify.data.model.User
import kotlinx.coroutines.launch
import com.example.foodify.data.model.Result
import kotlin.Exception

class LoginViewModel(
    private val authRepository: AuthenticationRepository
):ViewModel() {
    private val _loginState = MutableLiveData<Result<User>?>()
    val loginState: LiveData<Result<User>?> get() = _loginState


    private val _resetPasswordState = MutableLiveData<Result<Unit>?>()
    val resetPasswordState: LiveData<Result<Unit>?> get() = _resetPasswordState

    fun sendPasswordResetEmail(email: String) {
        _resetPasswordState.value = Result.Loading
        viewModelScope.launch {
            _resetPasswordState.value = authRepository.sendPasswordResetEmail(email)
        }
    }
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading // Show loading
            val result = authRepository.signInWithEmail(email, password)
            _loginState.value = result // Update UI state
        }
    }
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }


    fun logout() {
        authRepository.logout()
    }
    fun signInWithGoogle(
        credentialManager: CredentialManager,
        serverClientId: String,
        context: Context
    ) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            try {
                val credential = authRepository.getAuthCredential(credentialManager, serverClientId, context)

                if (credential != null) {
                    val result = authRepository.signInWithGoogle(credential)
                    _loginState.value = result
                } else {
                    _loginState.value = Result.Error(Exception("Google sign-in failed."))
                }
            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                _loginState.value = Result.Error(Exception("Sign-in cancelled by user."))
            } catch (e: Exception) {
                _loginState.value = Result.Error(e)
            }
        }
    }



}