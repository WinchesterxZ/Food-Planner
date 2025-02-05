package com.example.foodify.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodify.data.model.MealPreview

@Database(entities = [MealPreview::class], version = 1, exportSchema = false)
abstract class MealDatabase: RoomDatabase()  {
    companion object{
        private const val DATABASE_NAME = "meal_database"

    }
}