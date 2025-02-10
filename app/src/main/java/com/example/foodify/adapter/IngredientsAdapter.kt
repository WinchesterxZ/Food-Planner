package com.example.foodify.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodify.data.model.IngredientItem
import com.example.foodify.databinding.SearchItemBinding

class IngredientsAdapter() :
    ListAdapter<IngredientItem, IngredientsAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    inner class IngredientViewHolder(val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(ingredient: IngredientItem) {
            binding.progressBar.visibility = View.VISIBLE
            binding.apply {
                subName.text = "${ingredient.ingredient} - (${ingredient.measure})"
                val ingredientUrl = "https://www.themealdb.com/images/ingredients/${ingredient.ingredient}.png"
                image.load(ingredientUrl){
                    listener(
                        onSuccess = { _, _ -> progressBar.visibility = View.GONE },
                        onError = { _, _ -> progressBar.visibility = View.GONE }
                    )
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredient = getItem(position))
    }


    class IngredientDiffCallback : DiffUtil.ItemCallback<IngredientItem>() {
        override fun areItemsTheSame(oldItem: IngredientItem, newItem: IngredientItem): Boolean {
            return oldItem.ingredient == newItem.ingredient // Or whatever your unique ID is
        }

        override fun areContentsTheSame(oldItem: IngredientItem, newItem: IngredientItem): Boolean {
            return oldItem == newItem // Or compare relevant fields for changes
        }
    }
}