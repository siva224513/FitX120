package com.example.beginnerfit.domain.usecase.profile


import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import com.example.beginnerfit.EncryptDecryptManager
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.model.UserBackUpData
import com.example.beginnerfit.EncryptDecryptManager.encryptCBC
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class BackUpUserDataUseCase(private val repository: Repository) {

    suspend fun invoke() {


        val user = UserRepository.getUser()

        val userBackUpData = UserBackUpData(
            user,
            repository.getProgramSchedule(),
            repository.getAllProgramWorkout(),
            repository.getAllWorkoutLog(),
            repository.getAllFoodLogs(),
            repository.getAllWeightLogs(),
            repository.getAllWaterLogs(),
            repository.getAllSleepLogs()
        )

        val json = Json.encodeToString(UserBackUpData.serializer(), userBackUpData)

        println(json)



        val fileName = EncryptDecryptManager.getEncryptedFileName(user.email)



        val contextWrapper = ContextWrapper(MyApplication.getContext())
        val directory = contextWrapper.getDir("backup", MODE_PRIVATE)

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val backupFile = File(directory, fileName)
        BufferedWriter(FileWriter(backupFile)).use { writer ->
            writer.write(json)
        }


        println("data backup. .successful")


//       val jsonData =   backupFile.bufferedReader().use { it.readText()}
//
//
//        val userData = Json.decodeFromString(UserBackUpData.serializer(), jsonData)
//        println("user data")
//        println(jsonData)
//        println(userData)


    }



}