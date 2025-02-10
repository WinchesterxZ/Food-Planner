package com.example.foodify.calender.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.bookmarks.view.SyncState
import com.example.foodify.calender.view.MealPlanState
import com.example.foodify.data.model.MealPreview
import com.example.foodify.repository.FirestoreRepository
import com.example.foodify.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalenderViewModel(
    private val mealRepository: MealRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _mealState = MutableLiveData<MealPlanState?>()
    val mealState: MutableLiveData<MealPlanState?> = _mealState

    private val _syncState = MutableLiveData<SyncState>()
    val syncState: LiveData<SyncState> = _syncState


    fun syncCalendarMeals(userId: String) {
        _syncState.value = SyncState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val remoteMeals = firestoreRepository.syncCalendarMeals(userId)
                if(remoteMeals.isEmpty()){
                    withContext(Dispatchers.Main){
                        _syncState.value = SyncState.NoBackup("You Don`t Have Any Backup")
                    }
                    return@launch

                }
                val localMeals = mealRepository.getAllMealsWithDate(userId)
                if (localMeals.containsAll(remoteMeals)) {
                    withContext(Dispatchers.Main){
                        _syncState.value = SyncState.NoChange("Calendar is already up to date!")
                    }
                    return@launch
                }
                mealRepository.insertMeals(remoteMeals)
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Success("Calendar synced successfully!")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Error("Failed to sync calendar: ${e.message}")
                }
            }
        }
    }

    fun backupCalendarMeals(userId: String) {
        _syncState.value = SyncState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localMeals = mealRepository.getAllMealsWithDate(userId)
                val remoteMeals = firestoreRepository.syncCalendarMeals(userId)
                if (remoteMeals.containsAll(localMeals)) {
                    withContext(Dispatchers.Main){
                        _syncState.value = SyncState.NoChange("Calendar is already backed up!")
                    }

                    return@launch
                }

                firestoreRepository.backupMeals(userId, localMeals)
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Success("Calendar backed up successfully!")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Error("Failed to backup calendar: ${e.message}")
                }
            }
        }
    }


    fun getMealsByDay(day: String, userId: String) {
        _mealState.postValue(MealPlanState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val meals = mealRepository.getMealsByUserIdAndDate(day, userId)
                withContext(Dispatchers.Main) {
                    _mealState.value = MealPlanState.Success(meals)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.value = MealPlanState.Error(
                        type = MealPlanState.ErrorType.LoadError,
                        message = "An Error Occurred ${e.message}"
                    )

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
                        _mealState.value =
                            MealPlanState.Message(
                                action = MealPlanState.Action.ADDED_TO_CALENDER,
                                message = "Meal added to calender"
                            ) // Emit success state
                    } else {
                        _mealState.value =
                            MealPlanState.Error(
                                type = MealPlanState.ErrorType.ADDError,
                                message = "Failed to add meal to calender"
                            )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.value = MealPlanState.Error(
                        type = MealPlanState.ErrorType.ADDError,
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
                            MealPlanState.Message(
                                action = MealPlanState.Action.REMOVED_FROM_CALENDER,
                                message = "Meal removed from calender"
                            )
                    } else {
                        _mealState.value = MealPlanState.Error(
                            type = MealPlanState.ErrorType.DeleteError,
                            message = "Failed to delete meal from calender"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mealState.value = MealPlanState.Error(
                        type = MealPlanState.ErrorType.DeleteError,
                        message = "An Error Occurred ${e.message}"
                    ) // Emit error state
                }
            }
        }
    }

}