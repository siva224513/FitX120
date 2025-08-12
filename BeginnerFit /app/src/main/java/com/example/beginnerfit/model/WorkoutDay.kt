package com.example.beginnerfit.model

import java.time.LocalDate

data class WorkoutDay(
    val dayNumber:Int,
    val date: LocalDate,
    val dayName: String,
    val workouts: List<WorkOutDetail>,
    var title: String = "Title"
)
