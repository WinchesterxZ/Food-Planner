package com.example.foodify.data.model

import MealDetails

data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String,
    val strYoutube: String?,
    val strSource: String?,
    val strIngredient1: String?, val strIngredient2: String?, val strIngredient3: String?,
    val strIngredient4: String?, val strIngredient5: String?, val strIngredient6: String?,
    val strIngredient7: String?, val strIngredient8: String?, val strIngredient9: String?,
    val strIngredient10: String?, val strIngredient11: String?, val strIngredient12: String?,
    val strIngredient13: String?, val strIngredient14: String?, val strIngredient15: String?,
    val strIngredient16: String?, val strIngredient17: String?, val strIngredient18: String?,
    val strIngredient19: String?, val strIngredient20: String?,
    val strMeasure1: String?, val strMeasure2: String?, val strMeasure3: String?,
    val strMeasure4: String?, val strMeasure5: String?, val strMeasure6: String?,
    val strMeasure7: String?, val strMeasure8: String?, val strMeasure9: String?,
    val strMeasure10: String?, val strMeasure11: String?, val strMeasure12: String?,
    val strMeasure13: String?, val strMeasure14: String?, val strMeasure15: String?,
    val strMeasure16: String?, val strMeasure17: String?, val strMeasure18: String?,
    val strMeasure19: String?, val strMeasure20: String?,
    val isFavorite: Boolean,
    val userId: String?,
    val mealPlan: String?
) {
    // Mapping to MealPreview (Home & Bookmarks)
    fun toMealPreview() = MealPreview(
        idMeal = idMeal,
        strMeal = strMeal,
        strMealThumb = strMealThumb,
        isFav = isFavorite,
        userId = userId,
        mealPlan = mealPlan
    )

    // Mapping to MealDetails (Details Screen)
    fun toMealDetails() = MealDetails(
        id = idMeal,
        name = strMeal,
        category = strCategory,
        area = strArea,
        instructions = strInstructions.split("\r\n").filter { it.isNotBlank() }, // Split into list
        thumbnail = strMealThumb,
        youtube = strYoutube,
        source = strSource,
        ingredients =  (1..20).mapNotNull { index ->
            val ingredient = this::class.members.find { it.name == "strIngredient$index" }?.call(this) as? String
            val measure = this::class.members.find { it.name == "strMeasure$index" }?.call(this) as? String

            if (!ingredient.isNullOrBlank()) IngredientItem(ingredient, measure ?: "") else null
        },
        isFav = isFavorite, // Pass the isFav value
        userId = userId,
        mealPlan = mealPlan
    )
}
