package com.example.beginnerfit.domain.usecase.dashboard


import com.example.beginnerfit.model.Nutrition

class CalculateDailyTargetUseCase {


    companion object {
        const val FAT_LOSS = "FatLoss"
        const val BULKING = "Bulking"

    }

    fun invoke(maintenanceCalorie: Int, programType: String): Nutrition {


        val adjustCalorie = when (programType) {
            FAT_LOSS -> {
                (maintenanceCalorie - 250).coerceAtLeast(1500)
            }

            BULKING -> {
                (maintenanceCalorie + 250).coerceAtMost(4000)
            }

            else -> {
                maintenanceCalorie
            }
        }

        val protein = (adjustCalorie * 0.30 / 4)
        val carbs = (adjustCalorie * 0.40 / 4).toInt()
        val fat = (adjustCalorie * 0.20 / 9)
        val fiber = 30


        return Nutrition(
            adjustCalorie,
            protein,
            carbs,
            fat,
            fiber.toDouble()
        )


    }
}