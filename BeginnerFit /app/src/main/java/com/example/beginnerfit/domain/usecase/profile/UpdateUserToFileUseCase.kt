package com.example.beginnerfit.domain.usecase.profile

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.UserBackUpData

class UpdateUserToFileUseCase(private  val repository: Repository) {

   suspend fun invoke(data : UserBackUpData): Boolean{
        return repository.updateUserToFile(data)
    }
}