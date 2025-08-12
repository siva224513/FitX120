package com.example.beginnerfit.data

import android.content.ContentValues
import android.content.Context
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_FOOD_LOG_ID
import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_FOOD_LOG
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.FoodLog
import com.example.beginnerfit.model.FoodSchedule
import com.example.beginnerfit.model.Schedule
import kotlinx.serialization.json.Json

class FoodDao(databaseConnection: LocalDatabase) {
    private val writeableConnection = databaseConnection.writableDatabase
    private val readableConnection = databaseConnection.readableDatabase

    init {
        loadFoodListFromAssets(MyApplication.getContext())
    }

    fun loadFoodListFromAssets(context: Context) {
        var foodList: List<Food>?
        val json = context.assets.open("foods.json").bufferedReader().use { it.readText() }
        foodList = Json.decodeFromString(json)

        println("call from assets")
        insertAllFoodToDb(foodList!!)
    }


    fun insertAllFoodToDb(foods: List<Food>) {


        for (food in foods) {
            val query = """
             INSERT OR REPLACE INTO ${LocalDatabase.TABLE_FOOD} (
            ${LocalDatabase.COLUMN_FOOD_ID},
            ${LocalDatabase.COLUMN_FOOD_NAME},
            ${LocalDatabase.COLUMN_FOOD_CALORIE},
            ${LocalDatabase.COLUMN_FOOD_CARBS},
            ${LocalDatabase.COLUMN_FOOD_PROTEIN},
            ${LocalDatabase.COLUMN_FOOD_FAT},
            ${LocalDatabase.COLUMN_FOOD_FIBRE}
            )
            VALUES(?,?,?,?,?,?,?)
         """.trimIndent()

            val stmt = writeableConnection.compileStatement(query)

            stmt.bindLong(1, food.id.toLong())
            stmt.bindString(2, food.name)
            stmt.bindLong(3, food.calories.toLong())
            stmt.bindDouble(4, food.carbs)
            stmt.bindDouble(5, food.protein)
            stmt.bindDouble(6, food.fat)
            stmt.bindDouble(7, food.fiber)

            stmt.executeInsert()
            stmt.close()

            val foods = getAllFoods()

            println("printing all foods from db")
            for (i in foods) {
                println(i)
            }

        }
    }


    fun getAllFoods(): List<Food> {

        val foodList = mutableListOf<Food>()


        val cursor = readableConnection.rawQuery(
            "SELECT * FROM ${LocalDatabase.TABLE_FOOD}",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_NAME))
                val calorie =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_CALORIE))
                val carbs =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_CARBS))
                val protein =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_PROTEIN))
                val fat =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_FAT))
                val fiber =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_FIBRE))
                val food = Food(
                    id = id,
                    name = name,
                    calories = calorie,
                    carbs = carbs,
                    protein = protein,
                    fat = fat,
                    fiber = fiber
                )

                foodList.add(food)

            } while (cursor.moveToNext())
        }

        cursor.close()


        return foodList
    }


    fun insertFoodLog(foodLog: FoodLog) {
        val sql = """
        INSERT INTO ${LocalDatabase.TABLE_FOOD_LOG} 
        (${LocalDatabase.COLUMN_DATE}, ${LocalDatabase.COLUMN_MEAL_TYPE}, ${LocalDatabase.COLUMN_FOOD_ID}, ${LocalDatabase.COLUMN_DAY_ID})
        VALUES (?, ?, ?,?)
    """.trimIndent()

        val statement = writeableConnection.compileStatement(sql)

        statement.bindString(1, foodLog.date)
        statement.bindString(2, foodLog.mealType)
        statement.bindLong(3, foodLog.foodId.toLong())
        statement.bindLong(4, foodLog.dayId.toLong())

        val rowId = statement.executeInsert()
        if (rowId == -1L) {
            println("Failed to insert FoodLog using statement")
        } else {
            println("Inserted FoodLog using statement with ID: $rowId")
        }
    }


    fun getAllFoodLogs(): List<FoodLog> {
        val logs = mutableListOf<FoodLog>()
        val cursor = readableConnection.rawQuery(
            "SELECT * FROM ${LocalDatabase.TABLE_FOOD_LOG}", null
        )

        if (cursor.moveToFirst()) {
            do {
                val id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_LOG_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DATE))
                val mealType =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_MEAL_TYPE))
                val foodId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_ID))
                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val log = FoodLog(id, date, mealType, foodId, dayId)

                logs.add(log)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return logs
    }

    fun getAllFoodLogsByDate(date: String): List<FoodLog> {
        val logs = mutableListOf<FoodLog>()
        println("=========================================")
        println("getAllFoodLogsByDate is called for $date")
        println("=========================================")
        val cursor = readableConnection.rawQuery(
            "SELECT * FROM ${LocalDatabase.TABLE_FOOD_LOG} WHERE ${LocalDatabase.COLUMN_DATE} = ?",
            arrayOf(date)
        )

        if (cursor.moveToFirst()) {
            do {
                val id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_LOG_ID))
                val mealType =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_MEAL_TYPE))
                val foodId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_ID))
                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val log = FoodLog(id, date, mealType, foodId, dayId)

                println(log)

                logs.add(log)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return logs
    }

    fun getFoodSchedule(): List<FoodSchedule> {
        val schedules = mutableListOf<FoodSchedule>()
        val cursor = readableConnection.rawQuery(
            "SELECT * FROM ${LocalDatabase.TABLE_PROGRAM_SCHEDULE}",
            null
        )

        if (cursor.moveToFirst()) {
            do {

                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DATE))

                val schedule = FoodSchedule(dayId, date)


                schedules.add(schedule)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return schedules
    }

    fun getAllFoodLogsForProgressData(dayId: Int): List<FoodLog> {
        println("=========================================")
        println("getAllFoodLogsForProgressData is called for $dayId")
        println("=========================================")
        val logs = mutableListOf<FoodLog>()
        val cursor = readableConnection.rawQuery(
            "SELECT * FROM ${LocalDatabase.TABLE_FOOD_LOG} WHERE ${LocalDatabase.COLUMN_DAY_ID} <= ?",
            arrayOf(dayId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_LOG_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DATE))
                val mealType =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_MEAL_TYPE))
                val foodId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_FOOD_ID))
                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val log = FoodLog(id, date, mealType, foodId, dayId)
                logs.add(log)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return logs


    }

    fun deleteFoodLog(foodLog: FoodLog): Boolean {
        val rowsDeleted = writeableConnection.delete(
            TABLE_FOOD_LOG,
            "$COLUMN_FOOD_LOG_ID = ?",
            arrayOf(foodLog.id.toString())
        )
        return rowsDeleted > 0
    }


}