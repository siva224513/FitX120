package com.example.beginnerfit.domain.repository


import com.example.beginnerfit.model.User
import com.example.beginnerfit.data.LocalDatabaseHandler
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodLog
import com.example.beginnerfit.model.FoodSchedule
import com.example.beginnerfit.model.Schedule
import com.example.beginnerfit.model.Sleep
import com.example.beginnerfit.model.UserBackUpData
import com.example.beginnerfit.model.Water
import com.example.beginnerfit.model.Weight
import com.example.beginnerfit.model.WorkOutDetail
import com.example.beginnerfit.model.Workout
import com.example.beginnerfit.model.WorkoutLog
import kotlinx.coroutines.Dispatchers


object Repository {

    private var localDatabaseHandler: ApplicationRepository = LocalDatabaseHandler(Dispatchers.IO)


    suspend fun clearAllUserData() {
        localDatabaseHandler.clearAllUserData()
    }


    suspend fun insertInitialAllUserData(userData: UserBackUpData) {
        localDatabaseHandler.insertInitialAllUserData(userData)
    }

    suspend fun login(email: String, password: String): UserBackUpData? {
        return localDatabaseHandler.login(email, password)
    }

    suspend fun register(data: UserBackUpData): Boolean {
        return localDatabaseHandler.register(data)
    }


    suspend fun updateUserToFile(data: UserBackUpData): Boolean {
        return localDatabaseHandler.updateUserToFile(data)
    }

    suspend fun getPushOneWorkout(): List<Workout> {

        return localDatabaseHandler.getPushOneWorkout()

    }

    suspend fun getPushTwoWorkout(): List<Workout> {
        return localDatabaseHandler.getPushTwoWorkout()
    }


    suspend fun getPullOneWorkout(): List<Workout> {
        return localDatabaseHandler.getPullOneWorkout()
    }

    suspend fun getPullTwoWorkout(): List<Workout> {
        return localDatabaseHandler.getPullTwoWorkout()
    }


    suspend fun getLegsOneWorkout(): List<Workout> {
        return localDatabaseHandler.getLegsOneWorkout()
    }

    suspend fun getLegsTwoWorkout(): List<Workout> {
        return localDatabaseHandler.getLegsTwoWorkout()
    }

    suspend fun getRestDayWorkout(): List<Workout> {
        return localDatabaseHandler.getRestDayWorkout()
    }

    suspend fun updateUserProfile(user: User): Boolean {
        return localDatabaseHandler.updateUserProfile(user)
    }

    suspend fun insertFoodLog(foodLog: FoodLog) {
        return localDatabaseHandler.insertFoodLog(foodLog)
    }

    suspend fun getAllFoods(): List<Food> {
        return localDatabaseHandler.getAllFoods()
    }

    suspend fun getAllFoodLogsByDate(date: String): List<FoodLog> {
        return localDatabaseHandler.getAllFoodLogsByDate(date)
    }

    suspend fun saveWorkoutLog(
        workoutId: Int, dayId: Int, completedReps: String, weightUsed: String
    ): WorkOutDetail {
        return localDatabaseHandler.saveWorkoutLog(workoutId, dayId, completedReps, weightUsed)
    }

    suspend fun getAllWorkoutLogByDateAndId(date: String, workoutId: Int): List<WorkoutLog> {
        return localDatabaseHandler.getAllWorkoutLogByDateAndId(date, workoutId)
    }

    suspend fun insertProgramSchedule(dayNumber: Int, date: String, workoutType: String) {
        localDatabaseHandler.insertProgramSchedule(dayNumber, date, workoutType)
    }

    suspend fun insertWorkoutDayDetails(workout: Workout, dayNumber: Int) {
        localDatabaseHandler.insertWorkoutDayDetails(workout, dayNumber)
    }

    fun check(): List<FoodLog> {
        return localDatabaseHandler.check()
    }

    suspend fun getProgramSchedule(): List<Schedule> {
        return localDatabaseHandler.getProgramSchedule()
    }

    suspend fun getProgramWorkOutById(dayId: Int): List<WorkOutDetail> {
        return localDatabaseHandler.getProgramWorkOutById(dayId)
    }

    suspend fun getAllProgramWorkout(): List<WorkOutDetail> {
        return localDatabaseHandler.getAllProgramWorkout()
    }

    suspend fun getAllWorkoutsByCategory(workout: WorkOutDetail): List<WorkOutDetail> {
        return localDatabaseHandler.getAllWorkoutsByCategory(workout)
    }

    suspend fun getAllWorkoutLog(): List<WorkoutLog> {
        return localDatabaseHandler.getAllWorkoutLog()
    }

    suspend fun getAllFoodLogs(): List<FoodLog> {
        return localDatabaseHandler.getAllFoodLogs()
    }

    suspend fun getAllWeightLogs(): List<Weight> {
        return localDatabaseHandler.getAllWeightLogs()
    }

    suspend fun getAllWaterLogs(): List<Water> {
        return localDatabaseHandler.getAllWaterLogs()
    }

    suspend fun getAllSleepLogs(): List<Sleep> {
        return localDatabaseHandler.getAllSleepLogs()
    }

    suspend fun getAllProgramDates(): List<String> {
        return localDatabaseHandler.getAllProgramDates()
    }

    suspend fun saveDailyStrike(dayNumber: Int, date: String) {
        localDatabaseHandler.saveDailyStrike(dayNumber, date)
    }

    suspend fun getFoodSchedule(): List<FoodSchedule> {
        return localDatabaseHandler.getFoodSchedule()
    }

    suspend fun getDayIdByDate(date: String): Int? {
        return localDatabaseHandler.getDayIdByDate(date)
    }

    suspend fun getWaterCount(dayId: Int): Int {
        return localDatabaseHandler.getWaterCount(dayId)
    }

    suspend fun saveWaterCount(dayId: Int, waterCount: Int) {
        return localDatabaseHandler.saveWaterCount(dayId, waterCount)
    }

    suspend fun insertOrUpdateWeight(dayId: Int, weight: Double): Boolean {
        return localDatabaseHandler.insertOrUpdateWeight(dayId, weight)
    }

    suspend fun getWeight(dayId: Int): Double {
        return localDatabaseHandler.getWeight(dayId)
    }

    suspend fun getWaterCountForProgressData(dayId: Int): List<Water> {

        return localDatabaseHandler.getWaterCountForProgressData(dayId)
    }

    suspend fun getAllSleepLogsForProgressData(dayId: Int): List<Sleep> {
        return localDatabaseHandler.getAllSleepLogsForProgressData(dayId)
    }

    suspend fun getAllWorkoutLogsForProgressData(dayId: Int): List<WorkoutLog> {
        return localDatabaseHandler.getAllWorkoutLogsForProgressData(dayId)
    }

    suspend fun getAllWeightLogsForProgressData(dayId: Int): List<Weight> {
        return localDatabaseHandler.getAllWeightLogsForProgressData(dayId)
    }

    suspend fun getAllFoodLogsForProgressData(dayId: Int): List<FoodLog> {
        return localDatabaseHandler.getAllFoodLogsForProgressData(dayId)
    }

    suspend fun saveSleepLog(dayId: Int, achieved: Boolean) {
        localDatabaseHandler.saveSleepLog(dayId, achieved)
    }

    suspend fun getSleepLog(dayId: Int): Boolean {
        return localDatabaseHandler.getSleepLog(dayId)
    }

    suspend fun deleteFoodLog(foodLog: FoodLog): Boolean {
        return localDatabaseHandler.deleteFoodLog(foodLog)
    }

    suspend fun deleteUserBackupFile(email: String):Boolean?{
        return localDatabaseHandler.deleteUserBackupFile(email)
    }

   suspend  fun insertDemoAccount() {
       localDatabaseHandler.insertDemoAccount()
    }

}