package com.example.beginnerfit.domain.usecase.signup


import com.example.beginnerfit.domain.usecase.dashboard.CalculateMaintenanceCalorieUseCase
import com.example.beginnerfit.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompleteUserProfileUseCase(private val calculateMaintenanceCalorieUseCase: CalculateMaintenanceCalorieUseCase) {

     fun invoke(
        user: User,
        age: String,
        genderPosition: Int,
        height: String,
        currentWeight: String,
        targetWeight: String,
        programTypePosition: Int
    ): User {
        val ageInt = age.toIntOrNull()
        val heightFloat = height.toFloatOrNull()
        val currentWeightFloat = currentWeight.toFloatOrNull()
        val targetWeightFloat = targetWeight.toFloatOrNull()

        val gender = when (genderPosition) {
            1 -> "Male"
            2 -> "Female"
            else -> null
        }

        val programPlan = when (programTypePosition) {
            1 -> "Bulking"
            2 -> "FatLoss"
            3 -> "Maintenance"
            else -> null
        }

        val maintenanceCalorie = calculateMaintenanceCalorie(
            gender,
            ageInt,
            heightFloat,
            currentWeightFloat
        )

        return user.apply {
            this.age = ageInt
            this.gender = gender
            this.height = heightFloat?.toDouble()
            this.startWeight = currentWeightFloat?.toDouble()
            this.currentWeight = currentWeightFloat?.toDouble()
            this.targetWeight = targetWeightFloat?.toDouble()
            this.programPlan = programPlan
            this.maintenanceCalorie = maintenanceCalorie
            this.programStartDate = getCurrentDate()
        }


    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    private fun calculateMaintenanceCalorie(
        gender: String?,
        age: Int?,
        height: Float?,
        weight: Float?
    ): Int?{
        if (age == null || height == null || weight == null|| gender == null) return null

       return calculateMaintenanceCalorieUseCase.invoke(gender,age,height.toDouble(),weight.toDouble())
    }
}
