package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class FoodLog(
    val id: Int,
    val date: String,
    val mealType: String,
    val foodId: Int,
    val dayId:Int
)
