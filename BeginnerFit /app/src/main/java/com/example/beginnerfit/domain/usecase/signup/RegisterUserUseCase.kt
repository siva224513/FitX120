package com.example.beginnerfit.domain.usecase.signup

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.UserBackUpData

import com.example.beginnerfit.model.*

class RegisterUserUseCase(val repository: Repository) {
    suspend fun invoke(name: String, email: String, password: String): Boolean {


        val user = User(id = 0, name, email, password)
        val programSchedule = emptyList<Schedule>()
        val programWorkouts = emptyList<WorkOutDetail>()

        val workoutLogs = emptyList<WorkoutLog>()
        val foodLogs = emptyList<FoodLog>()
        val weightLogs = emptyList<Weight>()
        val waterLogs = emptyList<Water>()
        val sleepLogs = emptyList<Sleep>()
        val data = UserBackUpData(
            user,
            programSchedule,
            programWorkouts,
            workoutLogs,
            foodLogs,
            weightLogs,
            waterLogs,
            sleepLogs
        )
        return repository.register(data)
    }
}


//data class UserBackUpData(
//    val user:User,
//    val programSchedule: List<Schedule>,
//    val programWorkouts: List<WorkOutDetail>,
//    val workoutLogs :List<WorkoutLog>,
//    val foodLogs :List<FoodLog>,
//    val weightLogs :List<Weight>,
//    val waterLogs :List<Water>,
//    val sleepLogs :List<Sleep>
//)
