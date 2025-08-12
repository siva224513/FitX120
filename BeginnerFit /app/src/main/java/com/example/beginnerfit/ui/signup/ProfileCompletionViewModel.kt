package com.example.beginnerfit.ui.signup


import androidx.lifecycle.ViewModel
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.SessionManager
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.profile.UpdateUserToFileUseCase
import com.example.beginnerfit.domain.usecase.signup.CompleteUserProfileUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.InsertWorkoutWeeksUseCase
import com.example.beginnerfit.model.FoodLog
import com.example.beginnerfit.model.Schedule
import com.example.beginnerfit.model.Sleep
import com.example.beginnerfit.model.User
import com.example.beginnerfit.model.UserBackUpData
import com.example.beginnerfit.model.Water
import com.example.beginnerfit.model.Weight
import com.example.beginnerfit.model.WorkOutDetail
import com.example.beginnerfit.model.WorkoutLog
import java.time.LocalDate

class ProfileCompletionViewModel(
    private val completeUserProfileUseCase: CompleteUserProfileUseCase,
    private val insertWorkoutWeeksUseCase: InsertWorkoutWeeksUseCase,
    private val updateUserToFileUseCase: UpdateUserToFileUseCase
) : ViewModel() {

    var userAge: String = ""
    var genderPosition: Int = 0
    var userHeight: String = ""
    var currentWeight: String = ""
    var targetWeight: String = ""
    var programTypePosition: Int = 0


    suspend fun completeUser(user: User): Boolean {
        val updatedUser = completeUserProfileUseCase.invoke(
            user,
            userAge,
            genderPosition,
            userHeight,
            currentWeight,
            targetWeight,
            programTypePosition
        )






        val programSchedule = emptyList<Schedule>()
        val programWorkouts = emptyList<WorkOutDetail>()

        val workoutLogs = emptyList<WorkoutLog>()
        val foodLogs = emptyList<FoodLog>()
        val weightLogs = emptyList<Weight>()
        val waterLogs = emptyList<Water>()
        val sleepLogs = emptyList<Sleep>()
        val data = UserBackUpData(
            updatedUser,
            programSchedule,
            programWorkouts,
            workoutLogs,
            foodLogs,
            weightLogs,
            waterLogs,
            sleepLogs
        )
        if( updateUserToFileUseCase.invoke(data)){
            UserRepository.setUser(updatedUser)
            SessionManager.saveUser(MyApplication.getContext(),updatedUser)
            return true
        }
        return false

    }

    suspend fun generateWorkoutProgram() {
        insertWorkoutWeeksUseCase.invoke(LocalDate.now())
    }
}
