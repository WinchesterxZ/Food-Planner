package com.example.foodify.adapter.listeners

import com.example.foodify.data.model.MealPreview

interface OnItemClickListener {
    fun onBookmarkClick(meal: MealPreview)
}