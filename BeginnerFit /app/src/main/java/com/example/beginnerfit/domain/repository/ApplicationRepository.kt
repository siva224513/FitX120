package com.example.beginnerfit.domain.repository

import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodLog
import com.example.beginnerfit.model.FoodSchedule
import com.example.beginnerfit.model.Schedule
import com.example.beginnerfit.model.Sleep
import com.example.beginnerfit.model.User
import com.example.beginnerfit.model.UserBackUpData
import com.example.beginnerfit.model.Water
import com.example.beginnerfit.model.Weight
import com.example.beginnerfit.model.WorkOutDetail
import com.example.beginnerfit.model.Workout
import com.example.beginnerfit.model.WorkoutLog

interface ApplicationRepository {
    suspend fun login(email: String, password: String): UserBackUpData?
    suspend fun register(data : UserBackUpData): Boolean
    suspend fun insertInitialAllUserData(userData: UserBackUpData)
    suspend fun getPushOneWorkout(): List<Workout>
    suspend fun getPushTwoWorkout(): List<Workout>
    suspend fun getPullOneWorkout(): List<Workout>
    suspend fun getPullTwoWorkout(): List<Workout>
    suspend fun getLegsOneWorkout(): List<Workout>
    suspend fun getLegsTwoWorkout(): List<Workout>
    suspend fun getRestDayWorkout(): List<Workout>
    suspend fun updateUserProfile(user: User): Boolean
    suspend fun getAllFoods(): List<Food>
    suspend fun insertFoodLog(foodLog: FoodLog)
    suspend fun getAllFoodLogsByDate(date: String): List<FoodLog>
    suspend fun saveWorkoutLog(
        workoutId: Int,
        dayId: Int,
        completedReps: String,
        weightUsed: String
    ): WorkOutDetail

    suspend fun getAllWorkoutLogByDateAndId(date: String, workoutId: Int): List<WorkoutLog>
    suspend fun insertProgramSchedule(dayNumber: Int, date: String, workoutType: String)
    suspend fun insertWorkoutDayDetails(workout: Workout, dayNumber: Int)
    suspend fun getProgramSchedule(): List<Schedule>
    suspend fun getProgramWorkOutById(dayId: Int): List<WorkOutDetail>
    suspend fun getAllProgramWorkout(): List<WorkOutDetail>
    suspend fun getAllWorkoutLog(): List<WorkoutLog>
    suspend fun getAllFoodLogs(): List<FoodLog>
    suspend fun getAllWeightLogs(): List<Weight>
    suspend fun getAllWaterLogs(): List<Water>
    suspend fun getAllSleepLogs(): List<Sleep>
    suspend fun getAllProgramDates(): List<String>
    suspend fun saveDailyStrike(dayNumber: Int, date: String)
    suspend fun getFoodSchedule(): List<FoodSchedule>
    suspend fun clearAllUserData()
    suspend fun getAllWorkoutsByCategory(workout: WorkOutDetail): List<WorkOutDetail>
    fun check(): List<FoodLog>
    suspend fun getDayIdByDate(date: String): Int?
    suspend fun getWaterCount(dayId: Int): Int
    suspend fun saveWaterCount(dayId: Int, waterCount: Int)
    suspend fun getWeight(dayId: Int): Double
    suspend fun insertOrUpdateWeight(dayId: Int, weight: Double):Boolean
    suspend  fun getWaterCountForProgressData(dayId: Int): List<Water>
   suspend fun getAllSleepLogsForProgressData(dayId: Int): List<Sleep>
   suspend fun getAllWorkoutLogsForProgressData(dayId: Int): List<WorkoutLog>
  suspend  fun getAllWeightLogsForProgressData(dayId: Int): List<Weight>
  suspend  fun getAllFoodLogsForProgressData(dayId: Int): List<FoodLog>
    suspend fun updateUserToFile(data: UserBackUpData): Boolean
   suspend fun saveSleepLog(dayId: Int, achieved: Boolean)
   suspend fun getSleepLog(dayId: Int): Boolean
   suspend fun deleteFoodLog(foodLog: FoodLog): Boolean
   suspend  fun deleteUserBackupFile(email: String):Boolean?
  suspend  fun insertDemoAccount()
}