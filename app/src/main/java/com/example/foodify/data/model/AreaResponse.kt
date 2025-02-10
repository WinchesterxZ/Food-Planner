package com.example.foodify.data.model

import com.google.gson.annotations.SerializedName

data class AreaResponse(
    @SerializedName("meals")
    val areas: List<Area>
)