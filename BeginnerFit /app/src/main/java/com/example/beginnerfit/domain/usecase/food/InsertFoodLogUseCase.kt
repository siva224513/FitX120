package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.FoodLog

class InsertFoodLogUseCase(private val repository : Repository) {
    suspend  fun invoke(foodLog: FoodLog) {
        return repository.insertFoodLog(foodLog)
    }
}