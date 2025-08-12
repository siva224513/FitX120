package com.example.beginnerfit.domain.usecase.profile

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.UserBackUpData

class SetupUserDataFromJsonToDbUseCase (private val repository: Repository) {

    suspend fun invoke(userBackUpData: UserBackUpData){
        repository.insertInitialAllUserData(userBackUpData)
    }
}