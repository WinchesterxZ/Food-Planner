package com.example.foodify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.MealItemBinding
import com.example.foodify.home.view.OnBookmarkClickListener

class MealsAdapter(private val onBookmarkClick:OnBookmarkClickListener, private val onMealClick: (String) -> Unit) :
    ListAdapter<MealPreview, MealsAdapter.MealsViewHolder>(MealDiffCallback()) {

    inner class MealsViewHolder(val binding: MealItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: MealPreview) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                mealName.text = meal.strMeal
                mealImage.load(meal.strMealThumb) {
                    listener(
                        onSuccess = { _, _ -> progressBar.visibility = View.GONE },
                        onError = { _, _ -> progressBar.visibility = View.GONE }
                    )
                }
                bookmark.setImageResource(
                    if (meal.isFav) {
                        com.example.foodify.R.drawable.ic_bookmark_fill
                    } else {
                        com.example.foodify.R.drawable.ic_bookmark
                    }
                )
                bookmark.setOnClickListener {
                   onBookmarkClick.onBookmarkClick(meal)
                }
                root.setOnClickListener {
                    onMealClick(meal.idMeal)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder {
        val binding = MealItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) {
        holder.bind(getItem(position)) // Use getItem(position) from ListAdapter
    }


    class MealDiffCallback : DiffUtil.ItemCallback<MealPreview>() {
        override fun areItemsTheSame(oldItem: MealPreview, newItem: MealPreview): Boolean {
            return oldItem.idMeal == newItem.idMeal // Or whatever your unique ID is
        }

        override fun areContentsTheSame(oldItem: MealPreview, newItem: MealPreview): Boolean {
            return oldItem == newItem // Or compare relevant fields for changes
        }
    }
}