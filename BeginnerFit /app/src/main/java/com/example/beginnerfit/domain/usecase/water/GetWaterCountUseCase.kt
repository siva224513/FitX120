package com.example.beginnerfit.domain.usecase.water

import com.example.beginnerfit.domain.repository.Repository

class GetWaterCountUseCase(private val repository: Repository) {
   suspend fun invoke(dayId: Int): Int {
           return repository.getWaterCount(dayId)
    }
}