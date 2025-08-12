package com.example.beginnerfit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class WorkOutDetail(
    val id: Int,
    val name: String,
    val sets: Int,
    val reps: String,
    val weight: String,
    var completedReps:String,
    var weightUsed:String,
    val category: String,
    val url: String,
    val calorieBurned:Double,
    val dayId:Int,
    var isCompleted:Int
) : Parcelable
