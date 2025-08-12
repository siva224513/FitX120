package com.example.beginnerfit.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Workout(
    val id: Int =0,
    val name: String,
    val sets: Int,
    val reps: String,
    val weight:String,
    val category: String,
    val url: String
) : Parcelable