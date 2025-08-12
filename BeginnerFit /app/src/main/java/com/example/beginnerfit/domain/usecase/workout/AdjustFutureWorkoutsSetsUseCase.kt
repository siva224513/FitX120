package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.WorkOutDetail

class AdjustFutureWorkoutsSetsUseCase(private val repository: Repository) {


    suspend fun invoke(workout: WorkOutDetail) {

        val pastWorkouts = repository.getAllWorkoutsByCategory(workout)

        if (pastWorkouts.isEmpty() || pastWorkouts.size <2) return


        val pastRepsOne = pastWorkouts[0].completedReps.split("-").mapNotNull { it.toIntOrNull() }
        val pastRepsTwo = pastWorkouts[1].completedReps.split("-").mapNotNull { it.toIntOrNull() }


        val pastWeightOne = pastWorkouts[0].weightUsed.split("-").mapNotNull { it.toDoubleOrNull() }
        val pastWeightTwo = pastWorkouts[1].weightUsed.split("-").mapNotNull { it.toDoubleOrNull() }


        val targetReps = workout.reps.split("-").mapNotNull { it.toIntOrNull() }
        val targetWeight = workout.weight.split("-").mapNotNull { it.toDoubleOrNull() }

        println("AdjustFutureWorkoutsSetsUseCase is executed...")
        println("pastRepsOne: $pastRepsOne")
        println("pastRepsTwo: $pastRepsTwo")
        println("pastWeightOne: $pastWeightOne")
        println("pastWeightTwo: $pastWeightTwo")
        println("targetReps: $targetReps")
        println("targetWeight: $targetWeight")


        val newReps= mutableListOf<Int>()
        val newWeights = mutableListOf<Double>()



        val incrementWeight = 2.5
        val decrementWeight = 2.5
        val maxWeight = 100.0
        val minWeight = 2.5

        val minRep = 6
        val maxRep = 20


        for(i in targetReps.indices){
            val avgReps = (pastRepsOne[i] + pastRepsTwo[i]) / 2.0
            val avgWeight = (pastWeightOne[i] + pastWeightTwo[i]) / 2.0

            var newRep= targetReps[i]
            var newWeight = targetWeight[i]

            if(avgReps >=targetReps[i]){
                newWeight = (avgWeight+incrementWeight).coerceAtMost(maxWeight)
            }else if(avgReps < targetReps[i]-2){
                newWeight = (avgWeight-decrementWeight).coerceAtLeast(minWeight)
            }

            if(avgReps< targetReps[i]-3){
                newRep = (targetReps[i]-1).coerceAtLeast(minRep)
            }
            newReps.add(newRep)
            newWeights.add(String.format("%.1f",newWeight).toDouble())

        }

    }


}