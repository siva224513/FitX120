package com.example.beginnerfit.domain.usecase.food

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.Food


class GetAllFoodsUseCase(private val repository : Repository) {
    suspend  fun invoke(): List<Food> {
        return repository.getAllFoods()
    }
}
