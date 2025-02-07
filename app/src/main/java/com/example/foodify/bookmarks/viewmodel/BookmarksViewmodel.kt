package com.example.foodify.bookmarks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview
import com.example.foodify.home.view.MealState
import com.example.foodify.home.viewmodel.UserRepository
import com.example.foodify.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarksViewmodel(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository
):ViewModel() {


    private val _mealState = MutableLiveData<MealState>()
    val mealState: LiveData<MealState> = _mealState


    fun getSpecificFavMeals() {
        val userId = userRepository.getCurrentUser()?.uid
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.getFavMealsByUserId(userId ?: "guest")
                withContext(Dispatchers.Main) {
                    _mealState.postValue(MealState.Success(result))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.value = MealState.Error(
                        type = MealState.ErrorType.LoadError,
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


