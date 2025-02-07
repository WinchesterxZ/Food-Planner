package com.example.foodify.mealDetails.viewModel

import MealDetails
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                }
                meal.userId = userId
                _mealDetailsState.value = MealDetailsState.Success(meal)
            }

        }

    }

}