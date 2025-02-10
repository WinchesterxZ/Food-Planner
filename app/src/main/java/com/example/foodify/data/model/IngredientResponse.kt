package com.example.foodify.data.model

import com.google.gson.annotations.SerializedName

data class IngredientResponse(
    @SerializedName("meals")
    val ingredients: List<Ingredient>
)