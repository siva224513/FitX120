package com.example.beginnerfit.data

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.database.Cursor
import com.example.beginnerfit.EncryptDecryptManager
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_AGE
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_CURRENT_WEIGHT
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_EMAIL
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_GENDER
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_HEIGHT
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_ID
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_MAINTENANCE_CALORIE
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_NAME
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_PASSWORD
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_PROGRAM_START_DATE
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_PROGRAM_TYPE
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_TARGET_WEIGHT

import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_USER
import com.example.beginnerfit.model.User
import com.example.beginnerfit.model.UserBackUpData
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UserDao(databaseConnection: LocalDatabase) {

    private val writeableConnection = databaseConnection.writableDatabase
    private val readableConnection = databaseConnection.readableDatabase

    private fun checkUserExist(email: String): Boolean {

        val query = "SELECT * FROM $TABLE_USER WHERE ${COLUMN_EMAIL}= ?"
        val cursor = readableConnection.rawQuery(query, arrayOf(email))

        val exist = cursor.moveToNext()
        cursor.close()

        return exist
    }

    fun registerUser(name: String, email: String, password: String): Boolean {
        if (checkUserExist(email)) return false

        println("user not exist in the user table")

        val query =
            "INSERT INTO $TABLE_USER ($COLUMN_NAME,$COLUMN_EMAIL,$COLUMN_PASSWORD) VALUES (?,?,?)"
        writeableConnection.execSQL(query, arrayOf(name, email, password))

        println("user added successfully")
        return true
    }

    fun convertCursorToUser(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            do {

                val user =
                    User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_WEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TARGET_WEIGHT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROGRAM_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAINTENANCE_CALORIE))
                    )
                println("inside the getall()")
                println(user)

            } while (cursor.moveToNext())
        }
    }

    fun getAll() {
        println("-====================")
        println("getall user is called")
        println("-====================")
        val query = "SELECT * FROM $TABLE_USER"
        val cursor = readableConnection.rawQuery(query, null)

        convertCursorToUser(cursor)
    }

    fun updateUserProfile(user: User): Boolean {

        val values = ContentValues().apply {
            put(COLUMN_AGE, user.age)
            put(COLUMN_GENDER, user.gender)
            put(COLUMN_HEIGHT, user.height)
            put(LocalDatabase.COLUMN_START_WEIGHT, user.startWeight)
            put(COLUMN_CURRENT_WEIGHT, user.currentWeight)
            put(COLUMN_TARGET_WEIGHT, user.targetWeight)
            put(COLUMN_MAINTENANCE_CALORIE, user.maintenanceCalorie)
            put(COLUMN_PROGRAM_TYPE, user.programPlan)
            put(COLUMN_PROGRAM_START_DATE, getCurrentDate())
        }

        val rows = writeableConnection.update(
            TABLE_USER,
            values,
            "$COLUMN_ID = ?",
            arrayOf(user.id.toString())
        )


        getAll()

        return rows >= 0
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }


    fun getBackupFileIfExists(email: String): File? {
        val contextWrapper = ContextWrapper(MyApplication.getContext())
        val directory = contextWrapper.getDir("backup", MODE_PRIVATE)

        if (!directory.exists()) return null

        val fileName = EncryptDecryptManager.getEncryptedFileName(email)
        val backupFile = File(directory, fileName)


        return if (backupFile.exists()) backupFile else null
    }


    fun deleteUserBackupFile(email: String): Boolean {
        val file = getBackupFileIfExists(email)
        return file?.delete() ?: true
    }

    fun insertDemoData() {
        val json = MyApplication.getContext().assets.open("UserDemoAccount.json").bufferedReader()
            .use { it.readText() }
        val data = Json.decodeFromString(UserBackUpData.serializer(), json)
        saveUserToFile(data)
    }


    fun getUserDataFromFile(email: String): UserBackUpData? {
        val backupFile = getBackupFileIfExists(email) ?: return null

        return try {
            BufferedReader(FileReader(backupFile)).use { reader ->
                val json = reader.readText()
                Json.decodeFromString(UserBackUpData.serializer(), json)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateUserToFile(data: UserBackUpData): Boolean {
        val backUpFile = getBackupFileIfExists(data.user.email) ?: return false

        val json = Json.encodeToString(UserBackUpData.serializer(), data)

        return try {
            BufferedWriter(FileWriter(backUpFile)).use { writer ->
                writer.write(json)
            }
            true
        } catch (e: Exception) {
            false
        }

    }


    fun saveUserToFile(data: UserBackUpData): Boolean {

        val contextWrapper = ContextWrapper(MyApplication.getContext())
        val directory = contextWrapper.getDir("backup", Context.MODE_PRIVATE)

        if (!directory.exists()) directory.mkdirs()

        val fileName = EncryptDecryptManager.getEncryptedFileName(data.user.email)
        val file = File(directory, fileName)

        if (file.exists()) return false

        val json = Json.encodeToString(UserBackUpData.serializer(), data)

        return try {
            BufferedWriter(FileWriter(file)).use { writer ->
                writer.write(json)
            }
            true
        } catch (e: Exception) {
            false
        }

    }


    fun getUser(email: String, password: String): User? {

        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = readableConnection.rawQuery(query, arrayOf(email, password))
        var user: User? = null

        if (cursor.moveToFirst()) {

            user =
                User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_START_WEIGHT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_TARGET_WEIGHT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_PROGRAM_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAINTENANCE_CALORIE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_CURRENT_WEIGHT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROGRAM_START_DATE))
                )

            println("inside the getUser( ) in user dao")
            println("${user.id}, ${user.email}, ${user.password},${user.programPlan},${user.age}, ${user.gender}, ${user.height}, ${user.startWeight}, ${user.currentWeight}, ${user.targetWeight}, ${user.maintenanceCalorie}")

        } else {
            println("user not found  :Error here in getUser()")
        }
        cursor.close()

        return user

    }

    fun insertUser(user: User) {
        val values = ContentValues().apply {
            put(COLUMN_ID, user.id)
            put(COLUMN_NAME, user.name)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_AGE, user.age)
            put(COLUMN_GENDER, user.gender)
            put(COLUMN_HEIGHT, user.height)
            put(LocalDatabase.COLUMN_START_WEIGHT, user.startWeight)
            put(COLUMN_CURRENT_WEIGHT, user.currentWeight)
            put(COLUMN_TARGET_WEIGHT, user.targetWeight)
            put(COLUMN_MAINTENANCE_CALORIE, user.maintenanceCalorie)
            put(COLUMN_PROGRAM_TYPE, user.programPlan)
            put(COLUMN_PROGRAM_START_DATE, getCurrentDate())
        }
        writeableConnection.insert(TABLE_USER, null, values)
        println("user added successfully")
    }


}