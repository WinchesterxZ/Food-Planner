package com.example.foodify.bookmarks.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.bookmarks.view.SyncState
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview
import com.example.foodify.home.view.MealState
import com.example.foodify.home.viewmodel.UserRepository
import com.example.foodify.repository.FirestoreRepository
import com.example.foodify.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarksViewmodel(
    private val mealRepository: MealRepository,
    private val firestoreRepository: FirestoreRepository
):ViewModel() {


    private val _mealState = MutableLiveData<MealState>()
    val mealState: LiveData<MealState> = _mealState

    private val _syncState = MutableLiveData<SyncState>()
    val syncState: LiveData<SyncState> = _syncState


    fun syncBookmarkedMeals(userId: String) {
        _syncState.value = SyncState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val remoteMeals = firestoreRepository.syncBookmarkedMeals(userId)
                if(remoteMeals.isEmpty()){
                    withContext(Dispatchers.Main){
                        _syncState.value = SyncState.NoBackup("You Don`t Have Any Backup")
                    }
                    return@launch

                }
                val localMeals = mealRepository.getFavMealsByUserId(userId)
                if (localMeals.containsAll(remoteMeals)) {
                    withContext(Dispatchers.Main){
                        _syncState.value = SyncState.NoChange("Bookmarks are already up to date!")
                    }
                    return@launch
                }
                mealRepository.insertMeals(remoteMeals)
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Success("Bookmarks synced successfully!")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Error("Failed to sync bookmarks: ${e.message}")
                }

            }
        }
    }

    fun backupBookmarkedMeals(userId: String) {
        _syncState.value = SyncState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localMeals = mealRepository.getFavMealsByUserId(userId)
                val remoteMeals = firestoreRepository.syncBookmarkedMeals(userId)
                if (remoteMeals.containsAll(localMeals)) {
                    Log.d("aloo", "backupBookmarkedMeals: ")
                    withContext(Dispatchers.Main){
                        _syncState.value = SyncState.NoChange("Bookmarks are already backed up!")
                    }
                    return@launch
                }

                firestoreRepository.backupMeals(userId, localMeals)
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Success("Bookmarks backed up successfully!")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _syncState.value = SyncState.Error("Failed to backup bookmarks: ${e.message}")
                }
            }
        }
    }

    fun getSpecificFavMeals(userId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRepository.getFavMealsByUserId(userId)
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


