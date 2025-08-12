package com.example.beginnerfit.domain.usecase.weight

import com.example.beginnerfit.domain.repository.Repository


class SaveWeightUseCase(private val repository: Repository) {
    suspend fun invoke(dayId: Int, weight: Double): Boolean {
       return repository.insertOrUpdateWeight(dayId, weight)
    }
}