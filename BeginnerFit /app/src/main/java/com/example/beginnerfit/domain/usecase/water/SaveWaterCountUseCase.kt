package com.example.beginnerfit.domain.usecase.water

import com.example.beginnerfit.domain.repository.Repository

class SaveWaterCountUseCase (private val repository :Repository){
    suspend fun invoke(dayId: Int, waterCount: Int) {
        repository.saveWaterCount(dayId, waterCount)
    }
}