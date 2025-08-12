package com.example.beginnerfit.domain.usecase.profile

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.User


class DeleteUserBackupFileUseCase (private val repository: Repository) {

   suspend fun invoke(user: User) :Boolean?{
       return repository.deleteUserBackupFile(user.email)
    }
}