package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.FoodLog

class GetAllFoodLogsByDate(private val repository: Repository) {
    suspend fun invoke(date: String): List<FoodLog> {
        return repository.getAllFoodLogsByDate(date)
    }
}