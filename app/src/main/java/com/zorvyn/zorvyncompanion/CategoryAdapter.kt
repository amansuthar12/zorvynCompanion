package com.zorvyn.zorvyncompanion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zorvyn.zorvyncompanion.databinding.CategoryStatItemBinding
import java.text.NumberFormat
import java.util.Locale

data class CategoryStat(
    val category: String,
    val amount: Double,
    val percentage: Int,
    val color: Int,
    val iconRes: Int
)

class CategoryAdapter(private val stats: List<CategoryStat>) :
    RecyclerView.Adapter<CategoryAdapter.StatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val binding = CategoryStatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(stats[position])
    }

    override fun getItemCount() = stats.size

    inner class StatViewHolder(private val binding: CategoryStatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stat: CategoryStat) {
            binding.tvCategoryName.text = stat.category
            binding.tvPercentage.text = "${stat.percentage}%"
            
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            binding.tvAmount.text = formatter.format(stat.amount)
            
            binding.ivCategoryIcon.setImageResource(stat.iconRes)
            binding.progressBar.progress = stat.percentage
            binding.progressBar.setIndicatorColor(stat.color)
        }
    }
}
