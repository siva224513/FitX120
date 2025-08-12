package com.example.beginnerfit.ui.foodTrack.food


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beginnerfit.databinding.ItemFoodBinding
import com.example.beginnerfit.model.Food

class FoodAdapter(
    private val onItemClick: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallBack()) {


    private var allFoods = listOf<Food>()
    private var foodList = listOf<Food>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }


    fun updateAllFood(foods: List<Food>) {
        allFoods = foods
        foodList = foods
        submitList(foodList)
    }

    fun filterByName(query: String) {
        foodList = if (query.isBlank()) {
            allFoods
        } else {
            allFoods.filter { it.name.contains(query, ignoreCase = true) }
        }

        println("filter foods ${foodList.size}")

        submitList(foodList)
    }

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.foodName.text = food.name
            binding.calorie.text = "${food.calories} kcal"
            binding.root.setOnClickListener {
                onItemClick(food)
            }
        }
    }


}


private class FoodDiffCallBack : DiffUtil.ItemCallback<Food>() {
    override fun areItemsTheSame(
        oldItem: Food,
        newItem: Food
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Food,
        newItem: Food
    ): Boolean {
        return oldItem == newItem
    }

}
