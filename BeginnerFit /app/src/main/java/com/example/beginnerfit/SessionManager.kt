package com.example.beginnerfit

import android.content.Context
import android.content.SharedPreferences
import com.example.beginnerfit.model.User
import androidx.core.content.edit

object SessionManager {

    private const val PREF_NAME = "user_session"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_AGE = "age"
        private const val KEY_GENDER = "gender"
        private const val KEY_HEIGHT = "height"
        private const val KEY_START_WEIGHT = "startWeight"
        private const val KEY_TARGET_WEIGHT = "targetWeight"
        private const val KEY_PROGRAM_PLAN = "programPlan"
        private const val KEY_MAINTENANCE_CALORIE = "maintenanceCalorie"
        private const val KEY_CURRENT_WEIGHT = "currentWeight"
        private const val KEY_PROGRAM_START_DATE = "programStartDate"


    fun saveUser(context: Context, user: User) {
        getPrefs(context).edit {
            putInt(KEY_ID, user.id)
            putString(KEY_NAME, user.name)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASSWORD, user.password)
            putInt(KEY_AGE, user.age ?: 0)
            putString(KEY_GENDER, user.gender)
            putFloat(KEY_HEIGHT, user.height?.toFloat() ?: 0f)
            putFloat(KEY_START_WEIGHT, user.startWeight?.toFloat() ?: 0f)
            putFloat(KEY_TARGET_WEIGHT, user.targetWeight?.toFloat() ?: 0f)
            putString(KEY_PROGRAM_PLAN, user.programPlan)
            putInt(KEY_MAINTENANCE_CALORIE, user.maintenanceCalorie ?: 0)
            putFloat(KEY_CURRENT_WEIGHT, user.currentWeight?.toFloat() ?: 0f)
            putString(KEY_PROGRAM_START_DATE, user.programStartDate)
        }
    }

    fun getUser(context: Context): User? {
        val prefs = getPrefs(context)
        val id = prefs.getInt(KEY_ID, -1)
        if (id == -1) return null

        return User(
            id = id,
            name = prefs.getString(KEY_NAME, "") ?: "",
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            password = prefs.getString(KEY_PASSWORD, "") ?: "",
            age = prefs.getInt(KEY_AGE, 0),
            gender = prefs.getString(KEY_GENDER, null),
            height = prefs.getFloat(KEY_HEIGHT, 0f).toDouble(),
            startWeight = prefs.getFloat(KEY_START_WEIGHT, 0f).toDouble(),
            targetWeight = prefs.getFloat(KEY_TARGET_WEIGHT, 0f).toDouble(),
            programPlan = prefs.getString(KEY_PROGRAM_PLAN, null),
            maintenanceCalorie = prefs.getInt(KEY_MAINTENANCE_CALORIE, 0),
            currentWeight = prefs.getFloat(KEY_CURRENT_WEIGHT, 0f).toDouble(),
            programStartDate = prefs.getString(KEY_PROGRAM_START_DATE, null)
        )
    }

    fun clearUser(context: Context) {
        getPrefs(context).edit { clear() }
    }
}
