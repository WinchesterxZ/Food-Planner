package com.example.foodify.home.view

import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview

sealed class MealState {
    data object Loading : MealState() // Loading state


    data class Success(
        val meals: List<MealPreview>,
        val randomMeal: MealPreview = MealPreview.EMPTY,
        val categories: List<Category> = emptyList()
    ) : MealState()

    data class Message(
        val message: String,
        val action: Action
    ) : MealState()


    data class Error(
        val type: ErrorType,
        val message: String
    ) : MealState()

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