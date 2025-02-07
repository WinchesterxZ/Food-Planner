package com.example.foodify.di

import android.content.Context
import com.example.foodify.MainActivityViewModel
import com.example.foodify.authentication.viewmodel.LoginViewModel
import com.example.foodify.authentication.viewmodel.SignUpViewModel
import com.example.foodify.bookmarks.viewmodel.BookmarksViewmodel
import com.example.foodify.data.db.AuthLocalDataSource
import com.example.foodify.repository.AuthenticationRepository
import com.example.foodify.data.api.FirebaseService
import com.example.foodify.data.api.FirebaseServiceImpl
import com.example.foodify.data.api.RetrofitHelper
import com.example.foodify.data.db.MealDatabase
import com.example.foodify.home.viewmodel.HomeViewModel
import com.example.foodify.home.viewmodel.UserRepository
import com.example.foodify.mealDetails.viewModel.MealDetailsViewModel
import com.example.foodify.repository.MealRepository
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
    single { UserRepository(get())}
    // Provide ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel {BookmarksViewmodel(get(), get())}
    viewModel { MealDetailsViewModel(get(), get())}
}