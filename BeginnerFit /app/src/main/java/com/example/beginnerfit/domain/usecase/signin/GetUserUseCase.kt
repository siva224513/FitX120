package com.example.beginnerfit.domain.usecase.signin

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.User
import com.example.beginnerfit.model.UserBackUpData

class GetUserUseCase (val repository : Repository){

    suspend fun invoke(email: String, password: String):UserBackUpData? {
       return repository.login(email,password)
    }
}