package com.example.foodify.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview
import com.example.foodify.home.view.MealState
import com.example.foodify.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultViewModel(
    private val mealRepository: MealRepository
): ViewModel() {

    private val _mealState = MutableLiveData<MealState?>()
    val mealState: LiveData<MealState?> get() = _mealState

    // Store the original list of meals
    private val _meals = MutableLiveData<List<MealPreview>>()

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _filteredMeals = MutableLiveData<List<MealPreview>>()
    val filteredMeals: LiveData<List<MealPreview>> get() = _filteredMeals

    fun setSearchQuery(query: String) {
        _searchQuery.value = query.trim()
        filterData()
    }

    private fun filterData() {
        val query = _searchQuery.value.orEmpty()
        _filteredMeals.value = _meals.value?.filter {
            it.strMeal.contains(query, ignoreCase = true)
        }.orEmpty()
    }

    fun resetState() {
        _mealState.value = null
    }

    fun getMealsByCategory(category: String, userId: String) {
        _mealState.postValue(MealState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val savedMeals = mealRepository.getFavMealsByUserId(userId)
            val savedPlans = mealRepository.getAllMealsWithDate(userId)
            try {
                val meals = mealRepository.getMealsByCategory(category)
                withContext(Dispatchers.Main) {
                    meals.forEach { meal ->
                        meal.isFav = savedMeals.any { it.idMeal == meal.idMeal }
                        meal.mealPlan = savedPlans.find { it.idMeal == meal.idMeal }?.mealPlan ?: ""
                    }
                    _meals.value = meals
                    _mealState.postValue(MealState.Success(meals))
                    filterData()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.postValue(MealState.Error(MealState.ErrorType.LoadError, e.message ?: "Unknown error"))
                }
            }
        }
    }

    // Similar updates for getMealsByArea and getMealsByIngredient
    fun getMealsByArea(area: String, userId: String) {
        _mealState.postValue(MealState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val savedMeals = mealRepository.getFavMealsByUserId(userId)
            try {
                val meals = mealRepository.getMealsByArea(area)
                withContext(Dispatchers.Main) {
                    meals.forEach { meal ->
                        meal.isFav = savedMeals.any { it.idMeal == meal.idMeal }
                    }
                    _meals.value = meals
                    _mealState.postValue(MealState.Success(meals))
                    filterData()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.postValue(MealState.Error(MealState.ErrorType.LoadError, e.message ?: "Unknown error"))
                }
            }
        }
    }
    fun getMealsByName(mealName: String, userId: String) {
        _mealState.postValue(MealState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val savedMeals = mealRepository.getFavMealsByUserId(userId)
            try {
                val meals = mealRepository.searchMealByName(mealName)
                withContext(Dispatchers.Main) {
                    meals.forEach { meal ->
                        meal.isFav = savedMeals.any { it.idMeal == meal.idMeal }
                    }
                    _meals.value = meals
                    _mealState.postValue(MealState.Success(meals))
                    filterData()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.postValue(MealState.Error(MealState.ErrorType.LoadError, e.message ?: "Unknown error"))
                }
            }
        }
    }

    fun getMealsByIngredient(ingredient: String, userId: String) {
        _mealState.postValue(MealState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val savedMeals = mealRepository.getFavMealsByUserId(userId)
            try {
                val meals = mealRepository.getMealsByIngredient(ingredient)
                withContext(Dispatchers.Main) {
                    meals.forEach { meal ->
                        meal.isFav = savedMeals.any { it.idMeal == meal.idMeal }
                    }
                    _meals.value = meals
                    _mealState.postValue(MealState.Success(meals))
                    filterData()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.postValue(MealState.Error(MealState.ErrorType.LoadError, e.message ?: "Unknown error"))
                }
            }
        }
    }

    fun addMeal(meal: MealPreview, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.insertMeal(meal.copy(userId = userId))
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        // Update the meal in both original and filtered lists
                        updateMealFavoriteStatus(meal.idMeal, true)
                        _mealState.value = MealState.Message(
                            action = MealState.Action.ADDED_TO_FAVORITES,
                            message = "Meal added to bookmarks"
                        )
                    } else {
                        _mealState.value = MealState.Error(
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
                        // Update the meal in both original and filtered lists
                        updateMealFavoriteStatus(id, false)
                        _mealState.value = MealState.Message(
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
                    )
                }
            }
        }
    }

    private fun updateMealFavoriteStatus(mealId: String, isFavorite: Boolean) {
        // Update in original list
        _meals.value = _meals.value?.map { meal ->
            if (meal.idMeal == mealId) meal.copy(isFav = isFavorite) else meal
        }
        // Update in filtered list
        _filteredMeals.value = _filteredMeals.value?.map { meal ->
            if (meal.idMeal == mealId) meal.copy(isFav = isFavorite) else meal
        }
    }

    fun toggleFavButton(meal: MealPreview) {
        meal.isFav = !meal.isFav
    }
}