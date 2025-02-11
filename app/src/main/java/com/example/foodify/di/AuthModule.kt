package com.example.foodify.di

import android.content.Context
import com.example.foodify.main.MainActivityViewModel
import com.example.foodify.authentication.viewmodel.LoginViewModel
import com.example.foodify.authentication.viewmodel.SignUpViewModel
import com.example.foodify.bookmarks.viewmodel.BookmarksViewmodel
import com.example.foodify.calender.viewmodel.CalenderViewModel
import com.example.foodify.data.db.AuthLocalDataSource
import com.example.foodify.repository.AuthenticationRepository
import com.example.foodify.data.api.FirebaseService
import com.example.foodify.data.api.FirebaseServiceImpl
import com.example.foodify.data.api.RetrofitHelper
import com.example.foodify.data.db.MealDatabase
import com.example.foodify.home.viewmodel.HomeViewModel
import com.example.foodify.mealDetails.viewModel.MealDetailsViewModel
import com.example.foodify.repository.FirestoreRepository
import com.example.foodify.repository.MealRepository
import com.example.foodify.search.viewmodel.SearchResultViewModel
import com.example.foodify.search.viewmodel.SearchViewModel
import com.example.foodify.util.sharedPrefFileName
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single {
        androidContext().getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE)
    }
    single { AuthLocalDataSource(get()) }

    // Provide AuthLocalDataSource
    single { FirebaseAuth.getInstance() }
    // Provide AuthApiService
    single<FirebaseService> { FirebaseServiceImpl(get()) }

    // Provide AuthenticationRepository
    single { AuthenticationRepository(get(), get()) }
    single { MealDatabase.getInstance(androidContext()) }
    single { get<MealDatabase>().mealsDao() }  // Extract and provide MealDao
    single { RetrofitHelper.retrofitService }

    single { MealRepository(get(), get()) }
    single { FirestoreRepository() }
    // Provide ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel {BookmarksViewmodel(get(), get())}
    viewModel { MealDetailsViewModel(get())}
    viewModel { SearchViewModel(get()) }
    viewModel { SearchResultViewModel(get())}
    viewModel { CalenderViewModel(get(),get()) }

}