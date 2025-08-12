package com.example.beginnerfit.ui.signIn

import com.example.beginnerfit.model.User
import com.example.beginnerfit.model.UserBackUpData

sealed class SignInResult{

    class Success(val userBackUpData: UserBackUpData, val message: String) : SignInResult()
    class Failure(val message: String) : SignInResult()
}