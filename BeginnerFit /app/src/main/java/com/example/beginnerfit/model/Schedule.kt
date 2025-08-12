package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class Schedule(
    val dayId: Int,
    val date: String,
    val workoutType:String
)