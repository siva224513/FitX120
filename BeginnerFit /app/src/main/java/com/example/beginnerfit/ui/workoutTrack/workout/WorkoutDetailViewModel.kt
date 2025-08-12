package com.example.beginnerfit.ui.workoutTrack.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.workout.SaveDailyStreakUseCase
import com.example.beginnerfit.model.WorkOutDetail
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(private val saveDailyStreakUseCase: SaveDailyStreakUseCase) : ViewModel() {

    fun addStreak(dayNumber: Int, date: String) {
        viewModelScope.launch {
            saveDailyStreakUseCase.invoke(dayNumber,date)
        }
    }

    fun adjustFutureWorkoutsSets(workouts: List<WorkOutDetail>) {
        viewModelScope.launch {

        }
    }

}