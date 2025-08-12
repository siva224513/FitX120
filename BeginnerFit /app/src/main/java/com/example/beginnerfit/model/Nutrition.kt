package com.example.beginnerfit.model

data class Nutrition(
    val calorie: Int = 0,
    val protein: Double = 0.0,
    val carbs: Int = 0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0
){
    operator fun plus(other: Nutrition): Nutrition {
        return Nutrition(
            calorie + other.calorie,
            protein + other.protein,
            carbs + other.carbs,
            fat + other.fat,
            fiber + other.fiber
        )
    }

}