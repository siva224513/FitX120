package com.example.beginnerfit.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.dashboard.CalculateDailyTargetUseCase
import com.example.beginnerfit.domain.usecase.dashboard.GetProgramWeeksUseCase
import com.example.beginnerfit.domain.usecase.dashboard.GetProgressDataUseCase
import com.example.beginnerfit.domain.usecase.food.GetAllFoodLogsByDate
import com.example.beginnerfit.domain.usecase.food.GetTotalNutritionForMealUseCase
import com.example.beginnerfit.domain.usecase.weight.GetWeightForDayIdUseCase
import com.example.beginnerfit.model.DayProgress
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.Nutrition
import com.example.beginnerfit.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DashboardViewModel(
    private val getProgramWeeksUseCase: GetProgramWeeksUseCase,
    private val getTotalNutritionForMealUseCase: GetTotalNutritionForMealUseCase,
    private val getAllFoodLogsByDate: GetAllFoodLogsByDate,
    private val getWeightForDayIdUseCase: GetWeightForDayIdUseCase,
    private val calculateDailyTargetUseCase: CalculateDailyTargetUseCase,
    private val getProgressDataUseCase: GetProgressDataUseCase

) : ViewModel() {

    private val _programDates = MutableStateFlow<List<List<LocalDate?>>>(emptyList())
    val programDates: StateFlow<List<List<LocalDate?>>> get() = _programDates

    private var currentWeekIndex = 0
    private var weeks: List<List<LocalDate?>> = emptyList()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> get() = _selectedDate


    init {
        loadProgramDates()
    }

    suspend fun getDayId(date: String): Int? {
        return Repository.getDayIdByDate(date)
    }

    suspend fun progressData(dayId: Int?, user: User, date: String, foodList: List<Food>): List<DayProgress> {

        val progressDataList = getProgressDataUseCase.invoke(dayId!!, user,date,foodList)
        return progressDataList

    }

    private fun loadProgramDates() {

        if (weeks.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            weeks = getProgramWeeksUseCase.invoke()
            currentWeekIndex = getWeekIndexForDate(_selectedDate.value)
            _programDates.value = weeks
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        currentWeekIndex = getWeekIndexForDate(date)
    }

    fun getCurrentWeek(): List<LocalDate?> {
        return if (weeks.isNotEmpty()) weeks[currentWeekIndex] else emptyList()
    }

    fun goToNextWeek(): List<LocalDate?> {
        if (currentWeekIndex < weeks.lastIndex) currentWeekIndex++
        return getCurrentWeek()
    }

    fun goToPreviousWeek(): List<LocalDate?> {
        if (currentWeekIndex > 0) currentWeekIndex--
        return getCurrentWeek()
    }

    private fun getWeekIndexForDate(date: LocalDate): Int {
        return weeks.indexOfFirst { week -> week.any { it == date } }.takeIf { it != -1 } ?: 0
    }

    suspend fun getNutritionForDate(date: String, foods: List<Food>): Nutrition {

        val logs = getAllFoodLogsByDate.invoke(date)
        println(logs)
        val nutrition = getTotalNutritionForMealUseCase.invoke(logs, foods)
        println("Getting nutrition for date: $date , $nutrition")
        return nutrition
    }


    fun getFormattedDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return date.format(formatter)
    }


    suspend fun getCurrentWeightOfTheDay(currentDayId: Int?): Double {
        val currentWeight = getWeightForDayIdUseCase.invoke(currentDayId!!)
        println("Current weight for ${selectedDate.value}: $currentWeight")
        return currentWeight
    }


    fun calculateDailyTarget(maintenanceCalorie: Int, programPlan: String): Nutrition {
        val dailyTarget = calculateDailyTargetUseCase.invoke(maintenanceCalorie, programPlan)
        println("Daily target: $dailyTarget")
        return dailyTarget
    }


}
