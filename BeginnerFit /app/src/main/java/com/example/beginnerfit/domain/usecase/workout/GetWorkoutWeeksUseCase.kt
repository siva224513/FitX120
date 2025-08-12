package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.model.WorkoutDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class GetWorkoutWeeksUseCase(
    private val getProgramSchedule: GetProgramSchedule,
    private val getProgramWorkOutById: GetProgramWorkOutByIdUseCase
) {

    suspend fun invoke(): List<List<WorkoutDay>> {
        val schedules = getProgramSchedule.invoke()
        val allDays = mutableListOf<WorkoutDay>()
        for (schedule in schedules) {

            val dayNumber = schedule.dayId
            val date = schedule.date
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val parsedDate = LocalDate.parse(date,formatter)
            val dayName = parsedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val workouts = getProgramWorkOutById.invoke(dayNumber)

            val workoutDay = WorkoutDay(dayNumber,parsedDate,dayName,workouts).apply {
                title = schedule.workoutType
            }

            allDays.add(workoutDay)

        }
        val weeksList = mutableListOf<List<WorkoutDay>>()
        var index = 0
        while (index < allDays.size) {
            val week = mutableListOf<WorkoutDay>()
            while (index < allDays.size) {
                val currentDay = allDays[index]
                week.add(currentDay)
                index++
                if (currentDay.date.dayOfWeek == DayOfWeek.SUNDAY) break
            }
            weeksList.add(week)
        }
        return weeksList
    }

}

