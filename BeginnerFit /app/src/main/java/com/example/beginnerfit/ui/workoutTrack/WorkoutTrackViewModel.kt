package com.example.beginnerfit.ui.workoutTrack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.profile.CalculateStreakUseCase
import com.example.beginnerfit.domain.usecase.workout.GetWorkoutWeeksUseCase
import com.example.beginnerfit.model.WorkoutDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutTrackViewModel(
    private val getWorkoutWeeksUseCase: GetWorkoutWeeksUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase
) : ViewModel() {

    private val _weeks = MutableLiveData<List<List<WorkoutDay>>>()
    val weeks: LiveData<List<List<WorkoutDay>>> = _weeks

    private val _selectedWeekIndex = MutableLiveData<Int>()
    val selectedWeekIndex: LiveData<Int> = _selectedWeekIndex

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()


    private val _currentStreak = MutableLiveData<Int>()
    val currentStreak: LiveData<Int> = _currentStreak


    fun setSelectedWeek(index: Int) {
        _selectedWeekIndex.value = index
    }

    fun calculateStreak() {
        viewModelScope.launch {
            val currentStreak  = calculateStreakUseCase.invoke(selectedDate.value)
            _currentStreak.value = currentStreak
        }
    }
    fun loadSelectedDate(selectedDateStr: String) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val selectedLocalDate = LocalDate.parse(selectedDateStr, formatter)
            _selectedDate.value = selectedLocalDate

            val result = getWorkoutWeeksUseCase.invoke()
            _weeks.value = result

            val index = result.indexOfFirst { week ->
                week.any { it.date == selectedLocalDate }
            }

            _selectedWeekIndex.value = if (index >= 0) index else 0
        }
    }


}
