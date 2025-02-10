package com.example.foodify.repository


import com.example.foodify.data.model.MealPreview
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun syncBookmarkedMeals(userId: String): List<MealPreview> {
        val snapshot = db.collection("users").document(userId).collection("meals")
            .whereEqualTo("isFav", true)
            .get().await()

        return snapshot.documents.mapNotNull { it.toObject(MealPreview::class.java) }
    }

    suspend fun syncCalendarMeals(userId: String): List<MealPreview> {
        val snapshot = db.collection("users").document(userId).collection("meals")
            .whereNotEqualTo("mealPlan", "")
            .get().await()

        return snapshot.documents.mapNotNull { it.toObject(MealPreview::class.java) }
    }

    suspend fun backupMeals(userId: String, meals: List<MealPreview>) {
        val batch = db.batch()
        val mealsRef = db.collection("users").document(userId).collection("meals")

        meals.forEach { meal ->
            val mealDoc = mealsRef.document(meal.idMeal)
            batch.set(mealDoc, meal)
        }

        batch.commit().await()
    }


}
