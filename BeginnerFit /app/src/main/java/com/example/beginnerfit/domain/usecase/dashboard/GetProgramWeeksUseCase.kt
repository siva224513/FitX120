package com.example.beginnerfit.domain.usecase.dashboard

import com.example.beginnerfit.domain.repository.Repository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetProgramWeeksUseCase(val repository: Repository) {
    suspend fun invoke(): List<List<LocalDate?>> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val dates = repository.getAllProgramDates().map { LocalDate.parse(it,formatter) }.sorted()
        if (dates.isEmpty()) return emptyList()

        val allWeeks = mutableListOf<List<LocalDate?>>()

        var index = 0
        while (index < dates.size) {
            val week = MutableList<LocalDate?>(7) { null }
            val startDayIndex = dates[index].dayOfWeek.value % 7
            var dayIndex = startDayIndex

            while (index < dates.size && dayIndex < 7) {
                week[dayIndex] = dates[index]
                index++
                dayIndex++
            }

            allWeeks.add(week)
        }

        return allWeeks
    }

}