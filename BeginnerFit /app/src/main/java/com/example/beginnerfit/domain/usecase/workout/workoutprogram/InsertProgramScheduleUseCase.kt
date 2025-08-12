package com.example.beginnerfit.domain.usecase.workout.workoutprogram

import com.example.beginnerfit.domain.repository.Repository


class InsertProgramScheduleUseCase(val repository: Repository) {

    suspend fun invoke(dayNumber: Int, date: String, workoutType: String) {
        repository.insertProgramSchedule(dayNumber,date,workoutType)
    }
}