package com.example.foodify.home.view

import com.example.foodify.data.model.MealPreview

interface OnItemClickListener {
    fun onBookmarkClick(meal: MealPreview)
}