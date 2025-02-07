package com.example.foodify.mealDetails.viewModel

import MealDetails
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview

sealed class MealDetailsState {
    data object Loading : MealDetailsState() // Loading state


    data class Success(
        val meals: MealDetails,
    ) : MealDetailsState()

    data class Message(
        val message: String,
        val action: Action
    ) : MealDetailsState()


    data class Error(
        val type: ErrorType,
        val message: String
    ) : MealDetailsState()

    // Enum to distinguish between different actions
    enum class Action {
        ADDED_TO_FAVORITES, REMOVED_FROM_FAVORITES
    }
    sealed class ErrorType {
        data object LoadError : ErrorType()
        data object DeleteError : ErrorType()
        data object AddToFavoriteError : ErrorType()
    }
}