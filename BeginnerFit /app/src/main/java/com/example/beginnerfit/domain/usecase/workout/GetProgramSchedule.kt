package com.example.beginnerfit.domain.usecase.workout


import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.model.Schedule

class GetProgramSchedule (private val repository: Repository){

    suspend fun  invoke():List<Schedule>{
        return repository.getProgramSchedule()
    }
}