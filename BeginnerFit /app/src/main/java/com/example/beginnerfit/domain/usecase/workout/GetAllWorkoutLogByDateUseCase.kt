package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.WorkoutLog

class GetAllWorkoutLogByDateUseCase(val repository: Repository) {
    suspend fun invoke(date: String, id: Int) :List<WorkoutLog>{
         return repository.getAllWorkoutLogByDateAndId(date,id)
    }
}