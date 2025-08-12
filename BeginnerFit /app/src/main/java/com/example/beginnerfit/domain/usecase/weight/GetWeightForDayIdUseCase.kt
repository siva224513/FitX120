package com.example.beginnerfit.domain.usecase.weight

import com.example.beginnerfit.domain.repository.Repository

class GetWeightForDayIdUseCase(private val repository: Repository) {
     suspend fun invoke(dayId: Int): Double {
         return repository.getWeight(dayId)
     }
}
