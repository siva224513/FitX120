package com.example.beginnerfit.domain.usecase.workout.workoutprogram

import com.example.beginnerfit.domain.usecase.workout.GetWorkoutsForDayUseCase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class InsertWorkoutWeeksUseCase(
    private val getWorkoutForDayUseCase: GetWorkoutsForDayUseCase,
    private val insertProgramScheduleUseCase: InsertProgramScheduleUseCase,
    private val insertWorkoutDayDetails: InsertWorkoutDayDetailsUseCase
) {
    suspend fun invoke(startDate: LocalDate){
        val totalDays = 120
        for (i in 0 until totalDays) {
            val dayNumber = i + 1
            val date = startDate.plusDays(i.toLong())
            val workouts = getWorkoutForDayUseCase.invoke(date.dayOfWeek)

            val formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

            val workoutType = when (date.dayOfWeek) {
                DayOfWeek.MONDAY, DayOfWeek.THURSDAY -> "Push Day"
                DayOfWeek.TUESDAY, DayOfWeek.FRIDAY -> "Pull Day"
                DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY -> "Leg Day"
                DayOfWeek.SUNDAY -> "Active Rest Day"
            }

            insertProgramScheduleUseCase.invoke(dayNumber,formattedDate,workoutType)
            workouts.forEach {  workout ->
                insertWorkoutDayDetails.invoke(workout,dayNumber)
            }
        }

    }

}