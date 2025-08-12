package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodLog
import com.example.beginnerfit.model.Nutrition
import kotlinx.coroutines.flow.StateFlow


class GetTotalNutritionForMealUseCase() {

    fun  invoke(logs: List<FoodLog>, foods: List<Food>): Nutrition{

        if(logs.isEmpty()) return  Nutrition()

        println("GetTotalNutritionForMealUseCase is called $foods")
        var calorie = 0
        var protein = 0.0
        var carbs = 0
        var fat = 0.0
        var fiber = 0.0

        for (log in logs) {
            val food = foods.find { it.id == log.foodId } ?: continue
            calorie += food.calories
            protein += food.protein
            carbs += food.carbs.toInt()
            fat += food.fat
            fiber += food.fiber
        }
        val nutrition  = Nutrition(calorie, protein, carbs, fat, fiber)
        println("Nutrition: $nutrition")
        return nutrition
    }

}