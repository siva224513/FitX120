package com.example.beginnerfit.ui.foodTrack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.food.GetFoodWeeksUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate


class FoodTrackViewModel(
    private val getFoodWeeksUseCase: GetFoodWeeksUseCase
) : ViewModel() {

    private val _weeks = MutableLiveData<List<List<LocalDate>>>()
    val weeks: LiveData<List<List<LocalDate>>> = _weeks

    private val _selectedWeekIndex = MutableLiveData<Int>()
    val selectedWeekIndex: LiveData<Int> = _selectedWeekIndex

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate

    fun loadDates() {
        viewModelScope.launch {
            val weeks = getFoodWeeksUseCase.invoke()
            _weeks.value = weeks

            val today = LocalDate.now()
            val index = weeks.indexOfFirst { week -> week.any { it == today } }

            _selectedDate.value = if (index >= 0) today else weeks.lastOrNull()?.lastOrNull() ?: today
            _selectedWeekIndex.value = if (index >= 0) index else 0
        }
    }

    fun setSelectedWeek(index: Int) {
        _selectedWeekIndex.value = index
    }
}

