package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class WorkoutLog(
    val dayId: Int,
    val date: String
)