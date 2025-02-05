package com.example.foodify.data.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
   // data object LoggedOut : Result<Nothing>()
    //data object LoggedIn : Result<Nothing>()


}