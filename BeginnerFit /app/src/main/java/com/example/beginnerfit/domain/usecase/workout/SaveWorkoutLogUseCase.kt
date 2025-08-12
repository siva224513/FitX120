package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.WorkOutDetail

class SaveWorkoutLogUseCase(private val repository: Repository) {

    suspend fun invoke(workoutId: Int, dayId: Int, completedReps: String, weightUsed: String):WorkOutDetail{
        return repository.saveWorkoutLog(workoutId,dayId,completedReps,weightUsed)
    }
}