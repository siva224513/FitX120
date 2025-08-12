package com.example.beginnerfit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class User(
    val id: Int,
    var name: String,
    val email: String,
    var password: String,
    var age: Int? = 0,
    var gender: String? = null,
    var height: Double? = null,
    var startWeight: Double? = null,
    var targetWeight: Double? = null,
    var programPlan: String? = null,
    var maintenanceCalorie :Int? = 0,
    var currentWeight: Double? = null,
    var programStartDate :String?=null
) : Parcelable {
    fun isProfileCompleted(): Boolean {
        return this.age != 0
                && this.gender != null
                && this.height != null
                && this.startWeight != null
                && this.targetWeight != null
                && this.programPlan != null
                && this.maintenanceCalorie != 0
    }

}
