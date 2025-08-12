package com.example.beginnerfit.domain.usecase.workout

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.WorkOutDetail

class GetProgramWorkOutByIdUseCase(private val repository: Repository) {
    suspend fun invoke(dayId: Int): List<WorkOutDetail> {
        return repository.getProgramWorkOutById(dayId)
    }
}