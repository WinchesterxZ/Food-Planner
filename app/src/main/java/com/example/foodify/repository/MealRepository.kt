package com.example.foodify.repository

import MealDetails
import com.example.foodify.data.api.RetrofitService
import com.example.foodify.data.db.MealDao
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview

class MealRepository(
    private val retrofitService: RetrofitService,
    private val mealDao: MealDao
) {
    suspend fun getRandomMeal(): MealPreview{
        val response = retrofitService.getRandomMeal()
        if (response.isSuccessful){
           return response.body()?.meals?.get(0)?.toMealPreview() ?: MealPreview(
               idMeal = "",
               strMeal = "",
               strMealThumb = "",
               isFav = false,
               userId = ""
           )
        }else{
            throw Exception("Failed to fetch random meal")
        }
    }
    suspend fun getMealsCategory(): List<Category> {
        val response = retrofitService.getCategories()
        if (response.isSuccessful) {
            return response.body()?.categories ?: emptyList()
        } else {
            throw Exception("Failed to fetch categories")

        }
    }
    suspend fun getMealsByCategory(category: String): List<MealPreview> {
        val response = retrofitService.getMealsByCategory(category)
        if (response.isSuccessful) {
            return response.body()?.meals?.map { it.toMealPreview() } ?: emptyList()
        } else {
            throw Exception("Failed to fetch meals by category")
        }
    }
    suspend fun getMealsByArea(area: String): List<MealPreview>{
        val response = retrofitService.getMealsByArea(area)
        if (response.isSuccessful){
            return response.body()?.meals?.map { it.toMealPreview() } ?: emptyList()
        }else{
            throw Exception("Failed to fetch meals by area")
        }
    }
    suspend fun getMealDetails(id: String): MealDetails {
        val response = retrofitService.getMealById(id)
        if (response.isSuccessful) {
            return response.body()?.meals?.get(0)?.toMealDetails() ?: MealDetails.EMPTY
        }
        else{
                throw Exception("Failed to fetch meal details")
        }
    }

    suspend fun insertMeal(meal: MealPreview): Long {
        return mealDao.insertMeal(meal)
    }
    suspend fun insertMeals(meals: List<MealPreview>): List<Long> {
        return mealDao.insertMeals(meals)
    }
    suspend fun getAllMeals(): List<MealPreview> {
        return mealDao.getAllMeals()
    }
    suspend fun getFavMeals(): List<MealPreview> {
        return mealDao.getFavMeals()
    }
    suspend fun getFavMealsByUserId(userId: String): List<MealPreview> {
        return mealDao.getFavMealsByUserId(userId)
    }
    suspend fun getFavMealById(id: String , userId: String): MealPreview? {
        return mealDao.getMealById(id, userId)
    }
    suspend fun deleteMeal(id: String): Int {
        return mealDao.deleteMeal(id)
    }

}