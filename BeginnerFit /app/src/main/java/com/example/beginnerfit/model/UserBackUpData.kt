package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class UserBackUpData(
    val user:User,
    val programSchedule: List<Schedule>,
    val programWorkouts: List<WorkOutDetail>,
    val workoutLogs :List<WorkoutLog>,
    val foodLogs :List<FoodLog>,
    val weightLogs :List<Weight>,
    val waterLogs :List<Water>,
    val sleepLogs :List<Sleep>
)
