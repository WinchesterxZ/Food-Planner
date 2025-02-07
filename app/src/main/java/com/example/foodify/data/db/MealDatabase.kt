package com.example.foodify.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodify.data.model.MealPreview

@Database(entities = [MealPreview::class], version = 1, exportSchema = false)
abstract class MealDatabase: RoomDatabase()  {
    abstract fun mealsDao(): MealDao
    companion object{
        private const val DATABASE_NAME = "meal_database"

        @Volatile
        private var INSTANCE: MealDatabase? = null
        fun getInstance(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}