package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.domain.repository.Repository

class GetSleepLogUseCase(private val repository : Repository) {
    suspend fun invoke(dayId:Int):Boolean{
        return repository.getSleepLog(dayId)
    }
}