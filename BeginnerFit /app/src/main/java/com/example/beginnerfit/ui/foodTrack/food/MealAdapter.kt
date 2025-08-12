package com.example.beginnerfit.ui.foodTrack.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.ItemFoodLogBinding
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodLog

class MealFoodAdapter(
    private val allFoods: List<Food>,
    private val onDeleteClick: (FoodLog) -> Unit
) : ListAdapter<FoodLog, MealFoodAdapter.FoodViewHolder>(DiffCallback()) {

    inner class FoodViewHolder(private val binding: ItemFoodLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodLog: FoodLog) {
            val food = allFoods.find { it.id == foodLog.foodId } ?: return

            binding.tvFoodName.text = food.name
            binding.tvFoodCalories.text =
                binding.root.context.getString(R.string.nutrition_calorie, food.calories)

            binding.btnDeleteFood.setOnClickListener {
                onDeleteClick(foodLog)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodLog>() {
        override fun areItemsTheSame(oldItem: FoodLog, newItem: FoodLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodLog, newItem: FoodLog): Boolean {
            return oldItem == newItem
        }
    }
}
