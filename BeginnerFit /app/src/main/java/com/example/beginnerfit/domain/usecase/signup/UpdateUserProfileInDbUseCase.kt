package com.example.beginnerfit.domain.usecase.signup

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.User

class UpdateUserProfileInDbUseCase(private val repository: Repository) {
    suspend fun invoke(user: User): Boolean {
        return repository.updateUserProfile(user)
    }
}