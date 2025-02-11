package com.example.foodify.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodify.data.model.Area
import com.example.foodify.databinding.SearchItemBinding
import com.example.foodify.util.countryCodeMap

class AreaSearchAdapter(private val onCategoryClick: (String) -> Unit) :
    ListAdapter<Area, AreaSearchAdapter.AreaViewHolder>(AreaDiffCallback()) {

    inner class AreaViewHolder(val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(area: Area) {
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