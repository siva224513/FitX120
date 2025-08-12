package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetLegsOneWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetLegsTwoWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPullOneWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPullTwoWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPushOneWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPushTwoWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetRestDayWorkoutUseCase
import com.example.beginnerfit.model.Workout
import java.time.DayOfWeek


class GetWorkoutsForDayUseCase(
    val getPushOneWorkoutUseCase: GetPushOneWorkoutUseCase,
    val getPushTwoWorkoutUseCase: GetPushTwoWorkoutUseCase,
    val getPullOneWorkoutUseCase: GetPullOneWorkoutUseCase,
    val getPullTwoWorkoutUseCase: GetPullTwoWorkoutUseCase,
    val getLegsOneWorkoutUseCase: GetLegsOneWorkoutUseCase,
    val getLegsTwoWorkoutUseCase: GetLegsTwoWorkoutUseCase,
    val getRestDayWorkoutUseCase: GetRestDayWorkoutUseCase,
    val repository: Repository
) {
    suspend fun invoke(dayOfWeek: DayOfWeek): List<Workout> {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> getPushOneWorkoutUseCase.invoke()

            DayOfWeek.TUESDAY -> getPullOneWorkoutUseCase.invoke()

            DayOfWeek.WEDNESDAY -> getLegsOneWorkoutUseCase.invoke()

            DayOfWeek.THURSDAY -> getPushTwoWorkoutUseCase.invoke()

            DayOfWeek.FRIDAY -> getPullTwoWorkoutUseCase.invoke()

            DayOfWeek.SATURDAY -> getLegsTwoWorkoutUseCase.invoke()

            DayOfWeek.SUNDAY -> getRestDayWorkoutUseCase.invoke()
        }
    }
}