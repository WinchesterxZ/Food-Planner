package com.example.foodify.search.view

import com.example.foodify.data.model.Area
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.Ingredient

sealed class SearchState {
    data object Loading : SearchState() // Loading state


    data class SuccessCategories(
        val categories: List<Category>,
    ) : SearchState()

    data class SuccessAreas(
        val areas: List<Area>,
    ) : SearchState()
    data class SuccessIngredients(
        val ingredients: List<Ingredient>,
    ) : SearchState()

    data class Error(
        val type: ErrorType,
        val message: String
    ) : SearchState()

    // Enum to distinguish between different actions

    sealed class ErrorType {
        data object LoadError : ErrorType()
    }
}