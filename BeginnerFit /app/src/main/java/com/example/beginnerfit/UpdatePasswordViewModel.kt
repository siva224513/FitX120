package com.example.beginnerfit

import androidx.lifecycle.ViewModel
import com.example.beginnerfit.domain.usecase.signup.UpdateUserProfileInDbUseCase
import com.example.beginnerfit.model.User

class UpdatePasswordViewModel(val updateUserProfileInDbUseCase: UpdateUserProfileInDbUseCase) : ViewModel() {

    var typedPassword: String = ""

    suspend fun updatePassword(user: User): Boolean {

       return  updateUserProfileInDbUseCase.invoke(user)
    }
}
