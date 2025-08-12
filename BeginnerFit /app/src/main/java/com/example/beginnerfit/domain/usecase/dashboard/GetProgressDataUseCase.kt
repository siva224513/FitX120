package com.example.beginnerfit.domain.usecase.dashboard

import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.food.GetTotalNutritionForMealUseCase
import com.example.beginnerfit.model.DayProgress
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.User

class GetProgressDataUseCase(private val repository: Repository, private val getTotalNutritionForMealUseCase: GetTotalNutritionForMealUseCase) {

    suspend fun invoke(dayId: Int, user: User, date: String, foodList: List<Food>): List<DayProgress> {

        val progressData = mutableListOf<DayProgress>()


        val allWeightLogs = repository.getAllWeightLogsForProgressData(dayId)
        val allWaterLogs = repository.getWaterCountForProgressData(dayId)
        val allSleepLogs = repository.getAllSleepLogsForProgressData(dayId)
        val allWorkoutLogs = repository.getAllWorkoutLogsForProgressData(dayId)
        val allFoodLogs = repository.getAllFoodLogsForProgressData(dayId)


        val start = maxOf(1, dayId - 9)
        val dayList = (start..dayId).toList()
        for (i in dayList) {

            val weight = allWeightLogs.findLast { it.dayId <= i }?.weight ?: user.startWeight
            val water = allWaterLogs.find { it.dayId == i }?.glassCount?:0
            val sleep = allSleepLogs.find { it.dayId == i }?.isSleepAchieved?:false
            val workout = allWorkoutLogs.any { it.dayId == i }
            val nutrition = getTotalNutritionForMealUseCase.invoke(allFoodLogs.filter { it.dayId == i }, foodList)

            val calorie =  nutrition.calorie


            progressData.add(DayProgress(i.toString(), calorie.toString(),workout, water.toString(), weight.toString(),sleep ))
        }


        return progressData.reversed()
    }
}