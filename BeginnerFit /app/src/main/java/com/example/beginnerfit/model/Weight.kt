package com.example.beginnerfit.model

import kotlinx.serialization.Serializable


@Serializable
data class Weight(val dayId:Int,var weight: Double)
