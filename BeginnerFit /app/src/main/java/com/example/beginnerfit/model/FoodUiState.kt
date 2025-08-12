package com.example.beginnerfit.model

sealed class FoodUiState {
    object Loading : FoodUiState()
    data class Success(val foods: List<Food>) : FoodUiState()
}

