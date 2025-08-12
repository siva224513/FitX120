package com.example.beginnerfit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.signup.UpdateUserProfileInDbUseCase
import com.example.beginnerfit.model.User
import kotlinx.coroutines.launch

class ChangeUserNameViewModel(val updateUserProfileInDbUseCase: UpdateUserProfileInDbUseCase) :
    ViewModel() {

    var username: String = UserRepository.getUser().name
    suspend fun updateUserName(user: User): Boolean {

        return updateUserProfileInDbUseCase.invoke(user)

    }


}