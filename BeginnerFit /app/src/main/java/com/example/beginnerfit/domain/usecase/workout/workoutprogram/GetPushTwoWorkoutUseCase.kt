package com.example.beginnerfit.domain.usecase.workout.workoutprogram

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.Workout

class GetPushTwoWorkoutUseCase(val repository: Repository) {
    suspend fun invoke():List<Workout> {
      return  repository.getPushTwoWorkout()
    }
}