package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class Sleep(val dayId:Int, val isSleepAchieved: Boolean)