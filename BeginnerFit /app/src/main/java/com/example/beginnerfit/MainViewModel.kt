package com.example.beginnerfit


import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.MyApplication.Companion.PREF_NAME
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.example.beginnerfit.MainActivity.Companion.IS_FIRST_RUN

class MainViewModel : ViewModel() {
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    val user: User? = SessionManager.getUser(MyApplication.getContext())
    val repository = Repository

    init {
        viewModelScope.launch {
            if (isFirstRun()) {
                repository.insertDemoAccount()
                setFirstRun()
            }
            delay(50)
            _loading.value = false
        }
    }

    private fun isFirstRun(): Boolean {
        val prefs = MyApplication.getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        return prefs.getBoolean(IS_FIRST_RUN, true)
    }

    private fun setFirstRun() {
        val prefs = MyApplication.getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        prefs.edit { putBoolean(IS_FIRST_RUN, false) }
    }


}