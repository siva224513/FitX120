package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class Water(val dayId:Int,var glassCount:Int)
