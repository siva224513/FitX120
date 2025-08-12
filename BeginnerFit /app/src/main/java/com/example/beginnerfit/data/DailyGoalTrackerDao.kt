package com.example.beginnerfit.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_DAY_ID
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_GLASS_COUNT
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_SLEEP_ACHIEVED
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_WEIGHT_COUNT
import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_SLEEP
import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_WATER
import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_WEIGHT
import com.example.beginnerfit.model.Sleep
import com.example.beginnerfit.model.User
import com.example.beginnerfit.model.Water
import com.example.beginnerfit.model.Weight

class DailyGoalTrackerDao(databaseConnection: LocalDatabase) {
    private val writeableConnection = databaseConnection.writableDatabase
    private val readableConnection = databaseConnection.readableDatabase


    fun getAllWeightLogs(): List<Weight> {
        val query = """
            SELECT * FROM $TABLE_WEIGHT
        """.trimIndent()
        println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
        println("getAllWeightLogs is called..")
        println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
        val cursor = readableConnection.rawQuery(query, null)
        val weightLogs = mutableListOf<Weight>()
        if (cursor.moveToFirst()) {
            do {
                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_ID))
                val weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_COUNT))

                val log = Weight(dayId, weight)
                weightLogs.add(log)

                println(log)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return weightLogs

    }

    fun getAllWaterLogs(): List<Water> {
        val waterLogs = mutableListOf<Water>()
        val cursor = readableConnection.rawQuery("SELECT * FROM $TABLE_WATER", null)

        cursor.use {
            while (it.moveToNext()) {
                val dayId = it.getInt(it.getColumnIndexOrThrow(COLUMN_DAY_ID))
                val glassCount = it.getInt(it.getColumnIndexOrThrow(COLUMN_GLASS_COUNT))
                waterLogs.add(Water(dayId, glassCount))
            }
        }

        return waterLogs
    }

    fun getAllSleepLogs(): List<Sleep> {
        val sleepLogs = mutableListOf<Sleep>()
        val cursor = readableConnection.rawQuery("SELECT * FROM $TABLE_SLEEP", null)

        cursor.use {
            while (it.moveToNext()) {
                val dayId = it.getInt(it.getColumnIndexOrThrow(COLUMN_DAY_ID))
                val sleepAchieved = it.getInt(it.getColumnIndexOrThrow(COLUMN_SLEEP_ACHIEVED)) == 1
                sleepLogs.add(Sleep(dayId, sleepAchieved))
            }
        }

        return sleepLogs
    }


    fun getAllSleepLogsForProgressData(dayId: Int): List<Sleep> {
        println("getAllSleepLogsForProgressData is called.. with day id: $dayId")
        val query = """
        SELECT *
        FROM $TABLE_SLEEP
        WHERE $COLUMN_DAY_ID <= ? 
    """.trimIndent()
        val sleepLogs = mutableListOf<Sleep>()
        val cursor = readableConnection.rawQuery(query, arrayOf(dayId.toString()))

        if (cursor.moveToFirst()) {
            do {
                sleepLogs.add(
                    Sleep(
                        cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_SLEEP_ACHIEVED)) == 1
                    )
                )
            } while (cursor.moveToNext())

        }
        println("getAllSleepLogsForProgressData $sleepLogs")
        cursor.close()
        return sleepLogs
    }

    fun insertWeightLog(log: Weight) {
        val query = """
            INSERT OR REPLACE INTO ${LocalDatabase.TABLE_WEIGHT} (${LocalDatabase.COLUMN_DAY_ID},${LocalDatabase.COLUMN_WEIGHT_COUNT})
            VALUES (?,?)
        """.trimIndent()

        val statement = writeableConnection.compileStatement(query)
        statement.bindLong(1, log.dayId.toLong())
        statement.bindDouble(2, log.weight)
        val rowId = statement.executeInsert()

        if (rowId == -1L) {
            println("initial weight log insert failed")
        } else {
            println("initial weight log inserted successfully")
        }

    }

    fun insertWaterLog(dayId: Int, glassCount: Int) {
        val query = """
        INSERT OR REPLACE INTO ${LocalDatabase.TABLE_WATER} 
        (${LocalDatabase.COLUMN_DAY_ID}, ${LocalDatabase.COLUMN_GLASS_COUNT})
        VALUES (?, ?)
    """.trimIndent()

        val statement = writeableConnection.compileStatement(query)
        statement.bindLong(1, dayId.toLong())
        statement.bindLong(2, glassCount.toLong())
        val affectedRows = statement.executeUpdateDelete()

        if (affectedRows == 0) {
            println("Initial water log insert failed")
        } else {
            println("Initial water log inserted/updated successfully")
        }
    }


    fun insertSleepLog(log: Sleep) {

        val values = ContentValues().apply {
            put(LocalDatabase.COLUMN_DAY_ID, log.dayId)
            put(LocalDatabase.COLUMN_SLEEP_ACHIEVED, log.isSleepAchieved)
        }
        writeableConnection.insert(LocalDatabase.TABLE_SLEEP, null, values)
        println("sleep log inserted successfully")

    }

    fun getWaterCountForProgressData(dayId: Int): List<Water> {
        println("getWaterCountForProgressData is called.. with day id: $dayId")
        val query = """
        SELECT *
        FROM ${LocalDatabase.TABLE_WATER}
        WHERE $COLUMN_DAY_ID <= ? 
    """.trimIndent()

        val waterlog = mutableListOf<Water>()

        val cursor = readableConnection.rawQuery(query, arrayOf(dayId.toString()))

        if (cursor.moveToFirst()) {
            do {
                waterlog.add(
                    Water(
                        cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_GLASS_COUNT))
                    )
                )
            } while (cursor.moveToNext())
        }

        println("getWaterCountForProgressData $waterlog")

        cursor.close()
        return waterlog
    }

    fun getWaterCount(dayId: Int): Int {


        val query = """
        SELECT ${COLUMN_GLASS_COUNT}
        FROM $TABLE_WATER
        WHERE $COLUMN_DAY_ID = ?
    """.trimIndent()

        val cursor = readableConnection.rawQuery(query, arrayOf(dayId.toString()))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GLASS_COUNT))
        }

        cursor.close()
        return count
    }


    fun getAllWeightLogsForProgressData(dayId: Int): List<Weight> {
        println("getAllWeightLogsForProgressData is called.. with day id: $dayId")

        val query = """
        SELECT *
        FROM ${LocalDatabase.TABLE_WEIGHT}
        WHERE $COLUMN_DAY_ID <= ? 
    """.trimIndent()

        val weightLogs = mutableListOf<Weight>()
        val cursor = readableConnection.rawQuery(query, arrayOf(dayId.toString()))

        if (cursor.moveToFirst()) {
            do {
                weightLogs.add(
                    Weight(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_COUNT))
                    )
                )
            } while (cursor.moveToNext())
        }

        println("getAllWeightLogsForProgressData $weightLogs")

        cursor.close()
        return weightLogs
    }


    fun getWeight(dayId: Int): Double {
        val query = """
        SELECT $COLUMN_WEIGHT_COUNT, $COLUMN_DAY_ID
        FROM $TABLE_WEIGHT
        WHERE $COLUMN_DAY_ID <= ?
        ORDER BY $COLUMN_DAY_ID DESC
    """.trimIndent()

        println("=====================")
        println("getWeight is called.. with day id: $dayId")
        println("=====================")

        val cursor = readableConnection.rawQuery(query, arrayOf(dayId.toString()))


        val weight = if (cursor.moveToFirst()) {
            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_COUNT))
        } else 0.0

        cursor.close()
        return weight
    }


    fun insertOrUpdateWeight(dayId: Int, weight: Double): Boolean {

        println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
        println("insertOrUpdateWeight method is called: for :$dayId")
        val contentValues = ContentValues().apply {
            put(COLUMN_WEIGHT_COUNT, weight)
        }


        val rowsUpdated = writeableConnection.update(
            TABLE_WEIGHT,
            contentValues,
            "$COLUMN_DAY_ID = ?",
            arrayOf(dayId.toString())
        )
        println("rowsUpdated:$rowsUpdated")

        if (rowsUpdated == 0) {
            contentValues.put(COLUMN_DAY_ID, dayId)
            val rowId = writeableConnection.insert(TABLE_WEIGHT, null, contentValues)
            return rowId != -1L
        }

        println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
        getAllWeightLogs()


        return true
    }

    fun saveSleepLog(dayId: Int, achieved: Boolean) {

        val values = ContentValues().apply {
            put(COLUMN_SLEEP_ACHIEVED, if (achieved) 1 else 0)
        }
        val rowAffected = writeableConnection.update(
            TABLE_SLEEP,
            values,
            "$COLUMN_DAY_ID = ?",
            arrayOf(dayId.toString())
        )

        if (rowAffected == 0) {
            val insertValues = ContentValues().apply {
                put(COLUMN_DAY_ID, dayId)
                put(COLUMN_SLEEP_ACHIEVED, if (achieved) 1 else 0)
            }
            writeableConnection.insert(TABLE_SLEEP, null, insertValues)
        }

    }

    fun getSleepLog(dayId: Int): Boolean {

        var isSleepAchieved = false

        val query = """
            SELECT $COLUMN_SLEEP_ACHIEVED FROM $TABLE_SLEEP WHERE $COLUMN_DAY_ID = ?
        """.trimIndent()

        val cursor = readableConnection.rawQuery(query, arrayOf(dayId.toString()))



        if (cursor.moveToFirst()) {
            val sleepValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_ACHIEVED))
            isSleepAchieved = sleepValue == 1
        }

        cursor.close()
        return isSleepAchieved

    }

}