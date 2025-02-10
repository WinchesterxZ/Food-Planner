package com.example.foodify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.foodify.databinding.InstructionsItemBinding

class InstructionsAdapter(private val instructions:List<String>): RecyclerView.Adapter<InstructionsAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(val binding: InstructionsItemBinding): ViewHolder(binding.root) {
        fun bind(instruction: String){
            binding.apply {
                val index = instructions.indexOf(instruction) + 1
                instructionsNumber.text = index.toString()
                instructionText.text = instruction
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = InstructionsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(instructions[position])
    }

    override fun getItemCount(): Int {
        return instructions.size
    }
}