package com.example.foodify.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodify.data.model.Area
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.Ingredient
import com.example.foodify.repository.MealRepository
import com.example.foodify.search.view.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val mealRepository: MealRepository
):ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _filteredCategories = MutableLiveData<List<Category>>()
    val filteredCategories: LiveData<List<Category>> get() = _filteredCategories

    private val _filteredAreas = MutableLiveData<List<Area>>()
    val filteredAreas: LiveData<List<Area>> get() = _filteredAreas

    private val _filteredIngredients = MutableLiveData<List<Ingredient>>()
    val filteredIngredients: LiveData<List<Ingredient>> get() = _filteredIngredients

    fun setSearchQuery(query: String) {
        _searchQuery.value = query.trim()
        filterData()
    }

    private fun filterData() {
        val query = _searchQuery.value.orEmpty()
        when (val state = searchState.value) {
            is SearchState.SuccessCategories -> {
                _filteredCategories.value = state.categories.filter {
                    it.strCategory.contains(query, ignoreCase = true)
                }
            }
            is SearchState.SuccessAreas -> {
                _filteredAreas.value = state.areas.filter {
                    it.strArea.contains(query, ignoreCase = true)
                }
            }
            is SearchState.SuccessIngredients -> {
                _filteredIngredients.value = state.ingredients.filter {
                    it.strIngredient.contains(query, ignoreCase = true)
                }
            }
            else -> Unit
        }
    }
    fun getMealsCategories(){
        _searchState.postValue(SearchState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = mealRepository.getMealsCategory()
                withContext(Dispatchers.Main){
                    _searchState.postValue(SearchState.SuccessCategories(categories))
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _searchState.postValue(SearchState.Error(SearchState.ErrorType.LoadError, e.message ?: "Unknown error"))

                }
            }
        }
    }
    fun getMealsAreas(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val areas = mealRepository.getMealsArea()
                withContext(Dispatchers.Main){
                    _searchState.postValue(SearchState.SuccessAreas(areas))
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _searchState.postValue(SearchState.Error(SearchState.ErrorType.LoadError, e.message ?: "Unknown error"))

                }
            }
        }
    }
    fun getMealsIngredients(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ingredients = mealRepository.getMealsIngredients()
                withContext(Dispatchers.Main){
                    _searchState.postValue(SearchState.SuccessIngredients(ingredients))
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _searchState.postValue(SearchState.Error(SearchState.ErrorType.LoadError, e.message ?: "Unknown error"))

                }
            }
        }
    }
}