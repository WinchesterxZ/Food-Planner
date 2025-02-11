package com.example.foodify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodify.data.model.Category
import com.example.foodify.databinding.SearchItemBinding

class CategoriesSearchAdapter(private val onCategoryClick: (String) -> Unit) :
    ListAdapter<Category, CategoriesSearchAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                subName.text = category.strCategory
                val params = categoryCard.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(25, 25, 25, 25)
                categoryCard.layoutParams = params
                image.load(category.strCategoryThumb){
                    listener(
                        onSuccess = { _, _ -> progressBar.visibility = View.GONE },
                        onError = { _, _ -> progressBar.visibility = View.GONE }
                    )
                }
                root.setOnClickListener {
                    onCategoryClick(category.strCategory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(category = getItem(position))
    }


    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.idCategory == newItem.idCategory // Or whatever your unique ID is
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem // Or compare relevant fields for changes
        }
    }
}