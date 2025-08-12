package com.example.beginnerfit.domain.usecase.food


import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.FoodSchedule

class GetFoodScheduleUseCase(
    private val repository: Repository
) {
    suspend fun invoke(): List<FoodSchedule> {
        return repository.getFoodSchedule()
    }
}
