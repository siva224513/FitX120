package com.example.beginnerfit.ui.signup

sealed class SignUpResult(val message: String) {
    class Success(message: String) : SignUpResult(message)
    class Failure(message: String) : SignUpResult(message)
}