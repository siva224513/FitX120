package com.example.beginnerfit.ui.workoutTrack.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.workout.AdjustFutureWorkoutsSetsUseCase
import com.example.beginnerfit.domain.usecase.workout.SaveWorkoutLogUseCase
import com.example.beginnerfit.model.WorkOutDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutViewModel(
    private val saveWorkoutLogUseCase: SaveWorkoutLogUseCase,
    private val adjustFutureWorkoutsSetsUseCase: AdjustFutureWorkoutsSetsUseCase
) : ViewModel() {

    suspend fun saveWorkoutLog(
        workoutId: Int,
        dayId: Int,
        completedReps: String,
        weightUsed: String
    ): WorkOutDetail {
        return saveWorkoutLogUseCase.invoke(workoutId, dayId, completedReps, weightUsed)
    }

    fun adjustFutureWorkoutsSets(workout: WorkOutDetail) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                adjustFutureWorkoutsSetsUseCase.invoke(workout)
            }
        }
    }


}