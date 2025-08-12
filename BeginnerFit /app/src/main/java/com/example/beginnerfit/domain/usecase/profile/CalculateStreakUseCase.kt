package com.example.beginnerfit.domain.usecase.profile

import com.example.beginnerfit.domain.repository.Repository
import java.time.LocalDate

class CalculateStreakUseCase(private val repository: Repository) {

    suspend fun invoke(selectedDate: LocalDate): Int {


        val logs =
            repository.getAllWorkoutLog().filter { it.date <= selectedDate.toString() }
                .map { it.date }.map { LocalDate.parse(it) }
                .sortedDescending()


        if (logs.isEmpty()) return 0

        var streak = 0
        var i = 0

        if (logs[0] == selectedDate) {
            streak++
            i++
        }

        var expectedDate = selectedDate.minusDays(1)

        while (i < logs.size) {
            if (logs[i] == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
                i++
            } else {
                break
            }
        }

        println("CalculateStreakUseCase method $streak")

        return streak
    }
}







