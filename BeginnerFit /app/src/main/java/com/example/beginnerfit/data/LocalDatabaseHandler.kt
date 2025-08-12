package com.example.beginnerfit.data


import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import com.example.beginnerfit.EncryptDecryptManager
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.model.User
import com.example.beginnerfit.domain.repository.ApplicationRepository
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class LocalDatabaseHandler(private val dispatchers: CoroutineDispatcher) : ApplicationRepository {
    private val databaseConnection = LocalDatabase.getInstance(MyApplication.getContext())
    private val workoutDao = LocalWorkoutDao(databaseConnection)
    private val userDao = UserDao(databaseConnection)
    private val foodDao = FoodDao(databaseConnection)
    private val trackerDao = DailyGoalTrackerDao(databaseConnection)
    private val profileDao = ProfileDao(databaseConnection)


    override suspend fun clearAllUserData() {

        println("clearAllUserData is executed...")
        val db = databaseConnection.writableDatabase
        db.delete(LocalDatabase.TABLE_USER, null, null)
        db.delete(LocalDatabase.TABLE_PROGRAM_SCHEDULE, null, null)
        db.delete(LocalDatabase.TABLE_PROGRAM_WORKOUT, null, null)
        db.delete(LocalDatabase.TABLE_WORKOUT_LOG, null, null)
        db.delete(LocalDatabase.TABLE_FOOD_LOG, null, null)
        db.delete(LocalDatabase.TABLE_WEIGHT, null, null)
        db.delete(LocalDatabase.TABLE_WATER, null, null)
        db.delete(LocalDatabase.TABLE_SLEEP, null, null)


        val resetFoodLogQuery = """
            DELETE FROM sqlite_sequence WHERE name = '${LocalDatabase.TABLE_FOOD_LOG}'
        """.trimIndent()
        db.execSQL(resetFoodLogQuery)


    }

    override suspend fun getAllWorkoutsByCategory(workout: WorkOutDetail): List<WorkOutDetail> {
        return workoutDao.getAllWorkoutsByCategory(workout)
    }


    override fun check(): List<FoodLog> {
        return foodDao.getAllFoodLogs()
    }

    override suspend fun getDayIdByDate(date: String): Int? {
        return workoutDao.getDayIdByDate(date)
    }

    override suspend fun getWaterCount(dayId: Int): Int {
        return trackerDao.getWaterCount(dayId)
    }

    override suspend fun saveWaterCount(dayId: Int, waterCount: Int) {
        return trackerDao.insertWaterLog(dayId, waterCount)
    }

    override suspend fun getWeight(dayId: Int): Double {
        return trackerDao.getWeight(dayId)
    }

    override suspend fun insertOrUpdateWeight(dayId: Int, weight: Double): Boolean {
        return trackerDao.insertOrUpdateWeight(dayId, weight)
    }

    override suspend fun getWaterCountForProgressData(dayId: Int): List<Water> {
        return trackerDao.getWaterCountForProgressData(dayId)
    }

    override suspend fun getAllSleepLogsForProgressData(dayId: Int): List<Sleep> {
        return trackerDao.getAllSleepLogsForProgressData(dayId)
    }

    override suspend fun getAllWorkoutLogsForProgressData(dayId: Int): List<WorkoutLog> {
        return workoutDao.getAllWorkoutLogsForProgressData(dayId)
    }

    override suspend fun getAllWeightLogsForProgressData(dayId: Int): List<Weight> {
        return trackerDao.getAllWeightLogsForProgressData(dayId)
    }

    override suspend fun getAllFoodLogsForProgressData(dayId: Int): List<FoodLog> {
        return foodDao.getAllFoodLogsForProgressData(dayId)
    }


    override suspend fun insertInitialAllUserData(userData: UserBackUpData) {
        withContext(dispatchers) {

            userDao.insertUser(userData.user)

            for (schedule in userData.programSchedule) {
                workoutDao.insertProgramSchedule(schedule)
            }

            for (workout in userData.programWorkouts) {
                workoutDao.insertProgramWorkout(workout)
            }

            for (workoutLog in userData.workoutLogs) {
                workoutDao.insertWorkoutLog(workoutLog)
            }

            for (foodLog in userData.foodLogs) {
                foodDao.insertFoodLog(foodLog)
            }

            for (weightLog in userData.weightLogs) {
                trackerDao.insertWeightLog(weightLog)
            }

            for (waterLog in userData.waterLogs) {
                trackerDao.insertWaterLog(waterLog.dayId, waterLog.glassCount)
            }

            for (sleepLog in userData.sleepLogs) {
                trackerDao.insertSleepLog(sleepLog)
            }

        }
    }


    override suspend fun getPushOneWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getPushOneWorkout()
        }
    }

    override suspend fun getPushTwoWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getPushTwoWorkout()
        }
    }

    override suspend fun getPullOneWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getPullOneWorkout()
        }
    }

    override suspend fun getPullTwoWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getPullTwoWorkout()
        }
    }

    override suspend fun getLegsOneWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getLegsOneWorkout()
        }
    }

    override suspend fun getLegsTwoWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getLegsTwoWorkout()
        }
    }

    override suspend fun getRestDayWorkout(): List<Workout> {
        return withContext(dispatchers) {
            workoutDao.getRestDayWorkout()
        }
    }

    override suspend fun updateUserProfile(user: User): Boolean {
        return withContext(dispatchers) {
            userDao.updateUserProfile(user)
        }
    }

    override suspend fun getAllFoods(): List<Food> {
        return withContext(dispatchers) {
            foodDao.getAllFoods()
        }

    }

    override suspend fun insertFoodLog(foodLog: FoodLog) {
        withContext(dispatchers) {
            foodDao.insertFoodLog(foodLog)
        }
    }


    override suspend fun getAllFoodLogsByDate(date: String): List<FoodLog> {
        return withContext(dispatchers) {
            foodDao.getAllFoodLogsByDate(date)
        }
    }

    override suspend fun saveWorkoutLog(
        workoutId: Int, dayId: Int, completedReps: String, weightUsed: String
    ): WorkOutDetail {
        return withContext(dispatchers) {
            workoutDao.updateWorkoutProgress(workoutId, dayId, completedReps, weightUsed)

        }
    }

    override suspend fun getAllWorkoutLogByDateAndId(
        date: String,
        workoutId: Int
    ): List<WorkoutLog> {
        return emptyList()
    }

    override suspend fun insertProgramSchedule(
        dayNumber: Int,
        date: String,
        workoutType: String
    ) {
        workoutDao.insertProgramSchedule(Schedule(dayNumber, date, workoutType))
    }


    override suspend fun insertWorkoutDayDetails(workout: Workout, dayNumber: Int) {
        workoutDao.insertWorkoutDayDetails(workout, dayNumber)
    }

    override suspend fun getProgramSchedule(): List<Schedule> {
        return withContext(dispatchers) {
            workoutDao.getProgramSchedule()
        }
    }

    override suspend fun getProgramWorkOutById(dayId: Int): List<WorkOutDetail> {
        return withContext(dispatchers) {
            workoutDao.getProgramWorkOutById(dayId)
        }
    }

    override suspend fun getAllProgramWorkout(): List<WorkOutDetail> {
        return withContext(dispatchers) {
            workoutDao.getAllProgramWorkouts()
        }
    }

    override suspend fun getAllWorkoutLog(): List<WorkoutLog> {
        return withContext(dispatchers) {
            workoutDao.getAllWorkoutLog()
        }
    }

    override suspend fun getAllFoodLogs(): List<FoodLog> {
        return withContext(dispatchers) {
            foodDao.getAllFoodLogs()
        }
    }

    override suspend fun getAllWeightLogs(): List<Weight> {
        return withContext(dispatchers) {
            trackerDao.getAllWeightLogs()
        }
    }

    override suspend fun getAllWaterLogs(): List<Water> {
        return withContext(dispatchers) {
            trackerDao.getAllWaterLogs()
        }
    }

    override suspend fun getAllSleepLogs(): List<Sleep> {
        return withContext(dispatchers) {
            trackerDao.getAllSleepLogs()
        }
    }

    override suspend fun getAllProgramDates(): List<String> {
        return withContext(dispatchers) {
            profileDao.getAllProgramDates()
        }
    }

    override suspend fun saveDailyStrike(dayNumber: Int, date: String) {
        withContext(dispatchers) {
            workoutDao.saveDailyStrike(dayNumber, date)
        }
    }

    override suspend fun getFoodSchedule(): List<FoodSchedule> {
        return foodDao.getFoodSchedule()
    }


    override suspend fun login(email: String, password: String): UserBackUpData? {
        return withContext(dispatchers) {
            val userData = userDao.getUserDataFromFile(email)
            if (userData != null && userData.user.email == email && userData.user.password == password) {
                return@withContext userData
            } else {
                return@withContext null
            }
        }
    }


    override suspend fun updateUserToFile(data: UserBackUpData): Boolean {
        return withContext(dispatchers) {
            userDao.updateUserToFile(data)
        }
    }

    override suspend fun saveSleepLog(dayId: Int, achieved: Boolean) {
        return withContext(dispatchers) { trackerDao.saveSleepLog(dayId, achieved) }
    }

    override suspend fun getSleepLog(dayId: Int): Boolean {
        return withContext(dispatchers) { trackerDao.getSleepLog(dayId) }
    }

    override suspend fun deleteFoodLog(foodLog: FoodLog): Boolean {
        return withContext(dispatchers) { foodDao.deleteFoodLog(foodLog) }
    }

    override suspend fun deleteUserBackupFile(email: String): Boolean? {
        return withContext(dispatchers) { userDao.deleteUserBackupFile(email) }
    }

    override suspend fun insertDemoAccount() {
        withContext(dispatchers) { userDao.insertDemoData() }
    }


    override suspend fun register(
        data: UserBackUpData
    ): Boolean {
        return withContext(dispatchers) {
            userDao.saveUserToFile(data)
        }
    }


}