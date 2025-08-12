package com.example.beginnerfit.domain.usecase.dashboard

import com.example.beginnerfit.model.User

class CalculateMaintenanceCalorieUseCase {


    companion object {
        const val MALE = "Male"
        const val FEMALE = "Female"
    }

    fun invoke(gender: String, age: Int, height: Double, weight: Double): Int? {

        val bmr = when (gender) {
            MALE -> (10 * weight) + (6.25 * height) - (5 * age) + 5
            FEMALE -> (10 * weight) + (6.25 * height) - (5 * age) - 161
            else -> return null
        }

        println("BMR: $bmr")

        return (bmr * 1.55).toInt()
    }
}