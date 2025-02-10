package com.example.foodify.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodify.data.model.MealPreview
@Dao
interface MealDao {
    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<MealPreview>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealPreview):Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeals(meals: List<MealPreview>):List<Long>
    @Query("SELECT * FROM meals WHERE isFav = 1 AND userId = :userId")
    suspend fun getFavMealsByUserId(userId: String): List<MealPreview>
    @Query("DELETE FROM meals WHERE idMeal = :id")
    suspend fun deleteMeal(id: String):Int
    @Query("SELECT * FROM meals WHERE idMeal = :id AND userId = :userId")
    suspend fun getMealById(id: String , userId: String): MealPreview?
    @Query("SELECT * FROM meals WHERE userId = :userId And mealPlan = :date")
    suspend fun getMealsByUserIdAndDate(userId: String , date: String): List<MealPreview>
    @Query("SELECT * FROM meals WHERE userId = :userId AND mealPlan IS NOT NULL AND mealPlan != ''")
    fun getMealsWithDate(userId: String): List<MealPreview>

}