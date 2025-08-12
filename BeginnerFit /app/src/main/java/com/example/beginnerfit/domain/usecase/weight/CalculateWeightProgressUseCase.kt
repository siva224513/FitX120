package com.example.beginnerfit.domain.usecase.weight

import com.example.beginnerfit.model.User
import kotlin.math.roundToInt

class CalculateWeightProgressUseCase {

    fun invoke(current: Double, user: User):Int{

        println("inside calculateWeightProgressUseCase")
        println(current)

        if (current == 0.0) return 0
        val start = user.startWeight ?: 0.0
        val target = user.targetWeight ?: start


        val fatLoss = start > target
        val bulking = target > start


        var goal: Double?
        var progress: Double?

        when {
            fatLoss -> {
                goal = start - target
                progress = start - current

            }

            bulking -> {
                goal = target - start
                progress = current - start
            }

            else -> {
                return 0
            }
        }


       return progress.toInt()

    }
}