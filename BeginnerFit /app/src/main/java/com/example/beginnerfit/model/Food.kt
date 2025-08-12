package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class Food(
    val id: Int,
    val name: String,
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val fiber: Double
)
