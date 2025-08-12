package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.FoodLog

class DeleteFoodLogUseCase(private val repository: Repository) {
    suspend fun invoke(foodLog: FoodLog): Boolean {
        return repository.deleteFoodLog(foodLog)
    }
}