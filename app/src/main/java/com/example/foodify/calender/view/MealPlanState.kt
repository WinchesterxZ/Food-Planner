package com.example.foodify.calender.view
import com.example.foodify.data.model.MealPreview

sealed class MealPlanState {
    data object Loading : MealPlanState() // Loading state


    data class Success(
        val meals: List<MealPreview>,
    ) : MealPlanState()

    data class Message(
        val message: String,
        val action: Action
    ) : MealPlanState()


    data class Error(
        val type: ErrorType,
        val message: String
    ) : MealPlanState()

    // Enum to distinguish between different actions
    enum class Action {
        ADDED_TO_CALENDER, REMOVED_FROM_CALENDER
    }
    sealed class ErrorType {
        data object LoadError : ErrorType()
        data object DeleteError : ErrorType()
        data object ADDError : ErrorType()
    }
}