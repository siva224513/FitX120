package com.example.beginnerfit.domain.repository

import com.example.beginnerfit.model.User

object UserRepository {

    private var user: User? = null

    fun getUser(): User {
        return user ?: throw IllegalStateException("User not initialized")
    }

    fun setUser(value: User?) {
        user = value
    }

    fun clearUser() {
        user = null
    }
}
