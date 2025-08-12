package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.domain.repository.Repository

class SaveDailyStreakUseCase(val repository: Repository) {

    suspend fun invoke(dayNumber: Int, date: String) {
          repository.saveDailyStrike(dayNumber,date)
    }
}