package com.example.foodify.mealDetails.viewModel

import MealDetails
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.data.model.MealPreview
import com.example.foodify.home.view.MealState
import com.example.foodify.home.viewmodel.UserRepository
import com.example.foodify.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MealDetailsViewModel(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository
):ViewModel() {

    private val _mealDetailsState = MutableLiveData<MealDetailsState?>()
    val mealDetailsState: MutableLiveData<MealDetailsState?> = _mealDetailsState


    fun resetState() {
        _mealDetailsState.value = null
    }
    fun getMealDetails(mealId: String) {
        viewModelScope.launch (Dispatchers.IO){
            val user = userRepository.getCurrentUser()
            val userId = user?.uid ?: ""
            val savedMeal = mealRepository.getFavMealById(mealId, userId)
            val meal = mealRepository.getMealDetails(mealId)
            withContext(Dispatchers.Main){
                Log.d("a3a3a3a3", "getMealDetails: ${savedMeal?.userId } + ${savedMeal?.isFav}")
                if (savedMeal != null) {
                    meal.isFav = savedMeal.isFav
                    Log.d("teste", "getMealDetails: ${savedMeal?.userId } + ${savedMeal?.isFav} ${savedMeal.mealPlan}")
                    meal.mealPlan = savedMeal.mealPlan
                }
                meal.userId = userId
                _mealDetailsState.value = MealDetailsState.Success(meal)
            }

        }

    }

    fun addMealToCalender(meal: MealPreview) {
        val userId = userRepository.getCurrentUser()?.uid
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.insertMeal(meal.copy(userId = userId ?: "guest"))
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        _mealDetailsState.value =
                            MealDetailsState.Message(
                                action = MealDetailsState.Action.ADDED_TO_CALENDER,
                                message = "Meal added to calender"
                            ) // Emit success state
                    } else {
                        _mealDetailsState.value =
                            MealDetailsState.Error(
                                type = MealDetailsState.ErrorType.AddToCalenderError,
                                message = "Failed to add meal to Calender"
                            )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealDetailsState.value = MealDetailsState.Error(
                        type = MealDetailsState.ErrorType.AddToCalenderError,
                        message = "An Error Occurred ${e.message}"
                    )
                }
            }
        }
    }

    fun removeMealFromCalender(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.deleteMeal(id)
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        _mealDetailsState.value =
                            MealDetailsState.Message(
                                action = MealDetailsState.Action.REMOVED_FROM_CALENDER,
                                message = "Meal removed from Calender"
                            )
                    } else {
                        _mealDetailsState.value = MealDetailsState.Error(
                            type = MealDetailsState.ErrorType.DeleteFromCalenderError,
                            message = "Failed to delete meal from Calender"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealDetailsState.value = MealDetailsState.Error(
                        type = MealDetailsState.ErrorType.DeleteFromCalenderError,
                        message = "An Error Occurred ${e.message}"
                    ) // Emit error state
                }
            }
        }
    }

    fun addMealToBookMark(meal: MealPreview) {
        val userId = userRepository.getCurrentUser()?.uid
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.insertMeal(meal.copy(userId = userId ?: "guest"))
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        _mealDetailsState.value =
                            MealDetailsState.Message(
                                action = MealDetailsState.Action.ADDED_TO_FAVORITES,
                                message = "Meal added to bookmarks"
                            ) // Emit success state
                    } else {
                        _mealDetailsState.value =
                            MealDetailsState.Error(
                                type = MealDetailsState.ErrorType.AddToFavoriteError,
                                message = "Failed to add meal to bookmarks"
                            )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealDetailsState.value = MealDetailsState.Error(
                        type = MealDetailsState.ErrorType.AddToFavoriteError,
                        message = "An Error Occurred ${e.message}"
                    )
                }
            }
        }
    }

    fun removeMealFromBookMark(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.deleteMeal(id)
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        _mealDetailsState.value =
                            MealDetailsState.Message(
                                action = MealDetailsState.Action.REMOVED_FROM_FAVORITES,
                                message = "Meal removed from Bookmarks"
                            )
                    } else {
                        _mealDetailsState.value = MealDetailsState.Error(
                            type = MealDetailsState.ErrorType.DeleteFromFavoriteError,
                            message = "Failed to delete meal from Bookmarks"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealDetailsState.value = MealDetailsState.Error(
                        type = MealDetailsState.ErrorType.DeleteFromFavoriteError,
                        message = "An Error Occurred ${e.message}"
                    ) // Emit error state
                }
            }
        }
    }

    fun toggleFavButton(meal: MealDetails) {
        meal.isFav = !meal.isFav
    }

}