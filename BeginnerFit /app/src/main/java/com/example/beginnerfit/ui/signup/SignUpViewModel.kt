package com.example.beginnerfit.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.signup.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SignUpViewModel(val registerUserUseCase: RegisterUserUseCase) : ViewModel() {

    var name: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    private val _signUpResult = MutableSharedFlow<Boolean>(replay = 0)
    val signUpResult: SharedFlow<Boolean> get() = _signUpResult


    fun register() {
        viewModelScope.launch {
            var result = false
            try {
                result = registerUserUseCase.invoke(name,email,password)
            } catch (e: Exception) {
                println(" Exception Occurred in SignUpViewModel ${e.printStackTrace()}")
            }

            _signUpResult.emit(result)
        }
    }
}
