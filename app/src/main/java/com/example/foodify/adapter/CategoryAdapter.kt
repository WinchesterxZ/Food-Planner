package com.example.foodify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.foodify.data.model.Category
import com.example.foodify.databinding.CategoriesItemBinding

class CategoryAdapter(private val categories:List<Category>,private val onItemClick: (Category) -> Unit): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(val binding: CategoriesItemBinding): ViewHolder(binding.root) {
        fun bind(category: Category){
            binding.apply {
                progressBar.visibility = View.VISIBLE
                categoryName.text = category.strCategory

                categoryImage.load(category.strCategoryThumb){
                    listener(
                        onSuccess ={_,_ -> progressBar.visibility = View.GONE},
                        onError = {_,_ ->progressBar.visibility = View.GONE}
                    )
                }
                root.setOnClickListener {
                    onItemClick(category)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoriesItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}