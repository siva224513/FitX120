package com.example.beginnerfit

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository

class ClearAllUserDataUseCase(private val repository: Repository) {

    suspend fun invoke() {
        repository.clearAllUserData()
        UserRepository.clearUser()
        SessionManager.clearUser(MyApplication.getContext())
    }
}