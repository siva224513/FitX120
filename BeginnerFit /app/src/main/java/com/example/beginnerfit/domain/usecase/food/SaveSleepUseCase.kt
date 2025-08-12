package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.domain.repository.Repository

class SaveSleepUseCase(private val repository: Repository) {

   suspend fun invoke(dayId: Int, isAchieved: Boolean){
        repository.saveSleepLog(dayId,isAchieved)
    }
}


