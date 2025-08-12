package com.example.beginnerfit.ui.foodTrack.food

import com.example.beginnerfit.model.Food

sealed  class FoodResult {

    object  Loading: FoodResult()
    data class Success(val data: List<Food>) : FoodResult()
    data class Error(val message: String) :  FoodResult()

}