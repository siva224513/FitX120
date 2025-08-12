package com.example.beginnerfit.ui.foodTrack.food


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.dashboard.CalculateMaintenanceCalorieUseCase
import com.example.beginnerfit.domain.usecase.food.DeleteFoodLogUseCase
import com.example.beginnerfit.domain.usecase.food.GetAllFoodLogsByDate
import com.example.beginnerfit.domain.usecase.food.GetSleepLogUseCase
import com.example.beginnerfit.domain.usecase.food.GetTotalNutritionForMealUseCase
import com.example.beginnerfit.domain.usecase.food.InsertFoodLogUseCase
import com.example.beginnerfit.domain.usecase.food.SaveSleepUseCase
import com.example.beginnerfit.domain.usecase.signup.UpdateUserProfileInDbUseCase
import com.example.beginnerfit.domain.usecase.water.GetWaterCountUseCase
import com.example.beginnerfit.domain.usecase.water.SaveWaterCountUseCase
import com.example.beginnerfit.domain.usecase.weight.CalculateWeightProgressUseCase
import com.example.beginnerfit.domain.usecase.weight.GetWeightForDayIdUseCase
import com.example.beginnerfit.domain.usecase.weight.SaveWeightUseCase
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodLog
import com.example.beginnerfit.model.Nutrition
import com.example.beginnerfit.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FoodViewModel(
    private val calculateMaintenanceCalorieUseCase: CalculateMaintenanceCalorieUseCase,
    private val insertFoodLogUseCase: InsertFoodLogUseCase,
    private val getAllFoodLogsByDate: GetAllFoodLogsByDate,
    private val getTotalNutritionForMealUseCase: GetTotalNutritionForMealUseCase,
    private val todayDate: String,
    private val getWaterCountUseCase: GetWaterCountUseCase,
    private val saveWaterCountUseCase: SaveWaterCountUseCase,
    private val getWeightForDayIdUseCase: GetWeightForDayIdUseCase,
    private val saveWeightUseCase: SaveWeightUseCase,
    private val calculateWeightProgressUseCase: CalculateWeightProgressUseCase,
    private val updateUserProfileInDbUseCase: UpdateUserProfileInDbUseCase,
    private val sleepUseCase: SaveSleepUseCase,
    private val getSleepLogUseCase: GetSleepLogUseCase,
    private val deleteFoodLogUseCase: DeleteFoodLogUseCase

) : ViewModel() {


    private val _foodLogs = MutableStateFlow<List<FoodLog>>(emptyList())
    val foodLogs: StateFlow<List<FoodLog>> get() = _foodLogs

    private val _waterCount = MutableStateFlow(0)
    val waterCount: StateFlow<Int> get() = _waterCount

    private val _weight = MutableStateFlow(0.0)
    val weight: StateFlow<Double> get() = _weight


    private val _isSleepAchieved = MutableStateFlow(false)
    val isSleepAchieved: StateFlow<Boolean> get() = _isSleepAchieved


    init {
        viewModelScope.launch {
            loadFoodLogs(todayDate)
        }

    }


    fun loadWeight(dayId: Int) {
        viewModelScope.launch {
            _weight.value = getWeightForDayIdUseCase.invoke(dayId)
        }
    }


    suspend fun saveWeight(weight: Double, dayId: Int?): Boolean {

        _weight.value = weight
        val updated = saveWeightUseCase.invoke(dayId!!, weight)
        return updated

    }


    fun saveSleep(isAchieved: Boolean, dayId: Int?) {
        viewModelScope.launch {
            _isSleepAchieved.value = isAchieved
            sleepUseCase.invoke(dayId!!, isAchieved)
        }
    }

    suspend fun updateUserProfile(user: User): Boolean {
        return updateUserProfileInDbUseCase.invoke(user)
    }


    fun insertFoodLog(foodLog: FoodLog) {
        viewModelScope.launch {
            insertFoodLogUseCase.invoke(foodLog)
            loadFoodLogs(todayDate)
        }
    }

    suspend fun loadFoodLogs(today: String) {
        _foodLogs.value = getAllFoodLogsByDate.invoke(today)
    }


    fun loadWaterCount(dayId: Int) {
        viewModelScope.launch {
            _waterCount.value = getWaterCountUseCase.invoke(dayId)
        }
    }

    fun addGlass(dayId: Int?): Boolean = adjustWaterCount(1, dayId)

    fun removeGlass(dayId: Int?): Boolean = adjustWaterCount(-1, dayId)

    private fun adjustWaterCount(count: Int, currentDayId: Int?): Boolean {
        var reachedLimit = false
        viewModelScope.launch {
            currentDayId?.let { dayId ->
                val newCount = (_waterCount.value + count).coerceIn(0, 8)
                if (newCount == _waterCount.value) {
                    reachedLimit = true
                } else {
                    _waterCount.value = newCount
                    saveWaterCountUseCase.invoke(dayId, newCount)
                }
            }
        }
        return reachedLimit
    }

    fun getTotalNutritionFromDb(logs: List<FoodLog>, foods: List<Food>): Nutrition {
        return getTotalNutritionForMealUseCase.invoke(logs, foods)
    }


    fun getGoal(start: Double, target: Double): Double {

        return if (start > target) {
            start - target
        } else {
            target - start
        }
    }

    fun calculateWeightProgress(current: Double, user: User): Int {
        return calculateWeightProgressUseCase.invoke(current, user)
    }

    fun calculateMaintenanceCalorie(weight: Double, user: User): Int? {
        return calculateMaintenanceCalorieUseCase.invoke(
            user.gender!!,
            user.age!!,
            user.height!!,
            weight
        )
    }

    fun loadSleepLog(dayId: Int) {

        viewModelScope.launch {

            val result = getSleepLogUseCase.invoke(dayId)
            println("Sleep log loaded from DB: $result for dayId: $dayId")
            _isSleepAchieved.value = result
        }
    }

    fun deleteFoodLog(foodLog: FoodLog, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            val updated = deleteFoodLogUseCase.invoke(foodLog)
            onSuccess(updated)
            loadFoodLogs(todayDate)
        }
    }

}


