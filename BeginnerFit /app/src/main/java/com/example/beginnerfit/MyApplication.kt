package com.example.beginnerfit

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val isDark = prefs.getBoolean(KEY_DARK_MODE, false)
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        private lateinit var instance: MyApplication

        const val PREF_NAME = "app_prefs"
        const val KEY_DARK_MODE = "dark_mode"

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
}

