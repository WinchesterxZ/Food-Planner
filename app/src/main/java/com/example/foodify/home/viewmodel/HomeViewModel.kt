package com.example.foodify.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview
import com.example.foodify.home.view.MealState
import com.example.foodify.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _mealState = MutableLiveData<MealState?>()
    val mealState: MutableLiveData<MealState?> = _mealState

    private val _searchedMeals = MutableLiveData<List<MealPreview>>()
    val searchedMeals: LiveData<List<MealPreview>> = _searchedMeals

    fun resetState() {
        _mealState.value = null
    }

    fun loadHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            _mealState.postValue(MealState.Loading)

            try {
                val userId = userRepository.getCurrentUser()?.uid
                val planMealsDeferred = async { safeCall { mealRepository.getAllMealsWithDate(userId?:"") } }
                val favMealsDeferred = async { safeCall { mealRepository.getFavMealsByUserId(userId ?: "") } }
                val mealsDeferred = async { safeCall { mealRepository.getMealsByArea("Egyptian") } }
                val randomMealDeferred = async { safeCall { mealRepository.getRandomMeal() } }
                val categoriesDeferred = async { safeCall { mealRepository.getMealsCategory() } }

                val favMeals = favMealsDeferred.await() ?: emptyList()
                val planMeals = planMealsDeferred.await() ?: emptyList()
                val meals = mealsDeferred.await() ?: emptyList()
                val randomMeal = randomMealDeferred.await() ?: MealPreview.EMPTY
                val categories = categoriesDeferred.await() ?: emptyList()

                // Check if the API calls returned actual data
                if (meals.isNotEmpty() || randomMeal.idMeal.isNotEmpty() || categories.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        meals.forEach { meal ->
                            meal.isFav = favMeals.any { fav -> fav.idMeal == meal.idMeal }
                            meal.mealPlan = planMeals.find { it.idMeal == meal.idMeal }?.mealPlan ?: ""
                        }
                        randomMeal.isFav = favMeals.any { it.idMeal == randomMeal.idMeal }
                        randomMeal.mealPlan = planMeals.find { it.idMeal == randomMeal.idMeal }?.mealPlan ?: ""

                        _mealState.postValue(MealState.Success(meals, randomMeal, categories))

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _mealState.postValue(MealState.Error(MealState.ErrorType.LoadError, "No data available"))
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.postValue(MealState.Error(MealState.ErrorType.LoadError, e.message ?: "Unknown error"))
                }
            }
        }
    }


    private suspend fun <T> safeCall(action: suspend () -> T): T? {
        return try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null so the app doesn’t break but logs the failure
        }
    }







    fun addMeal(meal: MealPreview) {
            val userId = userRepository.getCurrentUser()?.uid
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = mealRepository.insertMeal(meal.copy(userId = userId ?: "guest"))
                    withContext(Dispatchers.Main) {
                        if (result > 0) {
                            _mealState.value =
                                MealState.Message(
                                    action = MealState.Action.ADDED_TO_FAVORITES,
                                    message = "Meal added to bookmarks"
                                ) // Emit success state
                        } else {
                            _mealState.value =
                                MealState.Error(
                                    type = MealState.ErrorType.AddToFavoriteError,
                                    message = "Failed to add meal to bookmarks"
                                )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _mealState.value = MealState.Error(
                            type = MealState.ErrorType.AddToFavoriteError,
                            message = "An Error Occurred ${e.message}"
                        )
                    }
                }
            }
        }

        fun removeMeal(id: String) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = mealRepository.deleteMeal(id)
                    withContext(Dispatchers.Main) {
                        if (result > 0) {
                            _mealState.value =
                                MealState.Message(
                                    action = MealState.Action.REMOVED_FROM_FAVORITES,
                                    message = "Meal removed from Bookmarks"
                                )
                        } else {
                            _mealState.value = MealState.Error(
                                type = MealState.ErrorType.DeleteError,
                                message = "Failed to delete meal from Bookmarks"
                            )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _mealState.value = MealState.Error(
                            type = MealState.ErrorType.DeleteError,
                            message = "An Error Occurred ${e.message}"
                        ) // Emit error state
                    }
                }
            }
        }

        fun toggleFavButton(meal: MealPreview) {
            meal.isFav = !meal.isFav
        }

    }
