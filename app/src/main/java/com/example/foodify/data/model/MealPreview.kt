package com.example.foodify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "meals")
data class MealPreview(
    @PrimaryKey
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    @get:PropertyName("isFav") @set:PropertyName("isFav")
    var isFav: Boolean,
    var  userId: String?,
    var mealPlan: String
) {
    // Firestore needs this for deserialization
    constructor() : this("", "", "", false, null, "")
    companion object {
        val EMPTY =MealPreview(
            idMeal = "",
            strMeal = "",
            strMealThumb = "",
            isFav = false,
            userId = null,
            mealPlan = ""
        )
    }
}