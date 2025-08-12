package com.example.beginnerfit.domain.usecase.food


import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetFoodWeeksUseCase(
    private val getFoodSchedule: GetFoodScheduleUseCase
) {
    suspend fun invoke(): List<List<LocalDate>> {
        val schedules = getFoodSchedule.invoke()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val allDates = schedules.map {
            LocalDate.parse(it.date, formatter)
        }.sorted()

        val weeksList = mutableListOf<List<LocalDate>>()
        var index = 0

        while (index < allDates.size) {
            val week = mutableListOf<LocalDate>()
            while (index < allDates.size) {
                val date = allDates[index]
                week.add(date)
                index++
                if (date.dayOfWeek == DayOfWeek.SUNDAY) break
            }
            weeksList.add(week)
        }

        return weeksList
    }
}
