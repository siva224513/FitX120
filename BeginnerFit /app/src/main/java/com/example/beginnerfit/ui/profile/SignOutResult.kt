package com.example.beginnerfit.ui.profile

sealed class SignOutResult {
    class Success(val message: String) : SignOutResult()
    class Failure(val message: String) : SignOutResult()
}