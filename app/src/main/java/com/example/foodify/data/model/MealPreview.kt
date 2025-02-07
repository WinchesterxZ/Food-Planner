package com.example.foodify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealPreview(
    @PrimaryKey
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    var isFav: Boolean,
    val  userId: String?
) {
    companion object {
        val EMPTY =MealPreview(
            idMeal = "",
            strMeal = "",
            strMealThumb = "",
            isFav = false,
            userId = null
        )
    }
}