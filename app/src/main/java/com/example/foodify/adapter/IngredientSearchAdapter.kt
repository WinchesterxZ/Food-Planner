package com.example.foodify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodify.data.model.Ingredient
import com.example.foodify.databinding.SearchItemBinding

class IngredientAdapter(private val onIngredientClick: (String) -> Unit) :
    ListAdapter<Ingredient, IngredientAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    inner class IngredientViewHolder(val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                subName.text = ingredient.strIngredient
                val params = categoryCard.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(25, 25, 25, 25)
                categoryCard.layoutParams = params
                val ingredientUrl = "https://www.themealdb.com/images/ingredients/${ingredient.strIngredient}.png"
                image.load(ingredientUrl){
                    listener(
                        onSuccess = { _, _ -> progressBar.visibility = View.GONE },
                        onError = { _, _ -> progressBar.visibility = View.GONE }
                    )
                }
                root.setOnClickListener {
                    onIngredientClick(ingredient.strIngredient)
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


    class IngredientDiffCallback : DiffUtil.ItemCallback<Ingredient>() {
        override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
            return oldItem.idIngredient == newItem.idIngredient // Or whatever your unique ID is
        }

        override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
            return oldItem == newItem // Or compare relevant fields for changes
        }
    }
}