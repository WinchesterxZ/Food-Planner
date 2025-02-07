package com.example.foodify.data.api

import com.example.foodify.data.model.AreaResponse
import com.example.foodify.data.model.CategoryResponse
import com.example.foodify.data.model.MealDetailsResponse
import com.example.foodify.data.model.MealPreview
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("random.php")
    suspend fun getRandomMeal(): Response<MealDetailsResponse>
    @GET("categories.php")
    suspend fun getCategories(): Response<CategoryResponse>
    @GET("list.php?a=list")
    suspend fun getArea(): Response<AreaResponse>
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): Response<MealDetailsResponse>
    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String): Response<MealDetailsResponse>
    @GET("search.php")
    suspend fun getMealByName(@Query("s") mealName: String): Response<MealDetailsResponse>
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") mealId: String): Response<MealDetailsResponse>



}