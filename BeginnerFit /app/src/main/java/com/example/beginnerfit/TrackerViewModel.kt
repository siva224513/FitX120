package com.example.beginnerfit

import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.data.LocalDatabaseHandler
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.food.GetAllFoodsUseCase
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodUiState
import com.example.beginnerfit.model.UserBackUpData
import com.example.beginnerfit.ui.foodTrack.food.FoodResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TrackerViewModel(private val getAllFoodsUseCase: GetAllFoodsUseCase) : ViewModel() {



    private val _foodsState = MutableStateFlow<FoodResult>(FoodResult.Loading)
    val foodsState: StateFlow<FoodResult> get() = _foodsState

    var allFoods = listOf<Food>()


    init {

        viewModelScope.launch {
            try {
                 allFoods = getAllFoodsUseCase.invoke()
                _foodsState.value = FoodResult.Success(allFoods)
            } catch (e: Exception) {
                _foodsState.value = FoodResult.Error("Exception occurred while getting foods")
            }
        }
    }


    fun isEditableDay(selectedDate: String, format: String = "yyyy-MM-dd"): Boolean {
        val formatter = DateTimeFormatter.ofPattern(format)
        val today = LocalDate.now()
        val date = LocalDate.parse(selectedDate, formatter)
        return date.isEqual(today) || date.isBefore(today)
    }


}