package com.example.foodify.adapter

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodify.data.model.Area
import com.example.foodify.databinding.SearchItemBinding

class AreaSearchAdapter(private val onCategoryClick: (String) -> Unit) :
    ListAdapter<Area, AreaSearchAdapter.AreaViewHolder>(AreaDiffCallback()) {

    inner class AreaViewHolder(val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(area: Area) {
            val countryCodeMap = mapOf(
                "American" to "us",
                "British" to "gb",
                "Canadian" to "ca",
                "Chinese" to "cn",
                "Croatian" to "hr",
                "Dutch" to "nl",
                "Egyptian" to "eg",
                "Filipino" to "ph",
                "French" to "fr",
                "Greek" to "gr",
                "Indian" to "in",
                "Irish" to "ie",
                "Italian" to "it",
                "Jamaican" to "jm",
                "Japanese" to "jp",
                "Kenyan" to "ke",
                "Malaysian" to "my",
                "Mexican" to "mx",
                "Moroccan" to "ma",
                "Polish" to "pl",
                "Portuguese" to "pt",
                "Russian" to "ru",
                "Spanish" to "es",
                "Thai" to "th",
                "Tunisian" to "tn",
                "Turkish" to "tr",
                "Ukrainian" to "ua",
                "Vietnamese" to "vn",
            )
            binding.apply {
                progressBar.visibility = View.VISIBLE
                subName.text = area.strArea
                val params = categoryCard.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(25, 25, 25, 25)
                categoryCard.layoutParams = params
                val countryCode = countryCodeMap[area.strArea] ?: "unknown"
                val flagUrl = if (countryCode == "unknown") {
                  "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Unknown_flag_-_European_version.png/640px-Unknown_flag_-_European_version.png"
                } else {
                    "https://flagcdn.com/w320/$countryCode.png"
                }
                Log.d("xxx", "bind: $flagUrl")
                image.load(flagUrl){
                    listener(
                        onSuccess = { _, _ -> progressBar.visibility = View.GONE },
                        onError = { _, _ -> progressBar.visibility = View.GONE }
                    )
                }
                root.setOnClickListener {
                    onCategoryClick(area.strArea)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bind(area = getItem(position))
    }


    class AreaDiffCallback : DiffUtil.ItemCallback<Area>() {
        override fun areItemsTheSame(oldItem: Area, newItem: Area): Boolean {
            return oldItem.strArea == newItem.strArea // Or whatever your unique ID is
        }

        override fun areContentsTheSame(oldItem: Area, newItem: Area): Boolean {
            return oldItem == newItem // Or compare relevant fields for changes
        }
    }
}