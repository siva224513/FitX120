package com.example.beginnerfit.domain.usecase.workout.workoutprogram

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.Workout


class InsertWorkoutDayDetailsUseCase(val repository : Repository) {

    suspend fun invoke(workout: Workout, dayNumber: Int) {
      repository.insertWorkoutDayDetails(workout,dayNumber)
    }
}