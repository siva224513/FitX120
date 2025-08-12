package com.example.beginnerfit.data

import android.content.Context
import android.database.Cursor
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_CATEGORY
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_DATE
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_DAY_ID
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_GLASS_COUNT
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_REPS
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_SETS
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_VIDEO_URL
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_WEIGHT_USED
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_WORKOUT_ID
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_WORKOUT_NAME
import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_WATER
import com.example.beginnerfit.model.Schedule
import com.example.beginnerfit.model.WorkOutDetail
import com.example.beginnerfit.model.Workout
import com.example.beginnerfit.model.WorkoutLog
import kotlinx.serialization.json.Json

class LocalWorkoutDao(databaseConnection: LocalDatabase) {

    private var cachedWorkoutList: List<Workout>? = null

    private val writableConnection = databaseConnection.writableDatabase
    private val readableConnection = databaseConnection.readableDatabase

    init {
        loadWorkoutListFromAssets(MyApplication.getContext())
    }

    fun loadWorkoutListFromAssets(context: Context): List<Workout> {

        if (cachedWorkoutList == null) {
            val json = context.assets.open("workouts.json").bufferedReader().use { it.readText() }
            cachedWorkoutList = Json.decodeFromString(json)
        }

        return cachedWorkoutList!!
    }


    fun getLegsTwoWorkout(): List<Workout> {
        return cachedWorkoutList?.filter { it.category == "Legs-2" } ?: emptyList()
    }


    fun getLegsOneWorkout(): List<Workout> {
        return cachedWorkoutList?.filter { it.category == "Legs-1" } ?: emptyList()
    }

    fun getPullTwoWorkout(): List<Workout> {

        return cachedWorkoutList?.filter { it.category == "Pull-2" } ?: emptyList()
    }


    fun getPullOneWorkout(): List<Workout> {
        return cachedWorkoutList?.filter { it.category == "Pull-1" } ?: emptyList()
    }

    fun getPushTwoWorkout(): List<Workout> {
        return cachedWorkoutList?.filter { it.category == "Push-2" } ?: emptyList()
    }


    fun getPushOneWorkout(): List<Workout> {
        return cachedWorkoutList?.filter { it.category == "Push-1" } ?: emptyList()
    }

    fun getRestDayWorkout(): List<Workout> {
        return cachedWorkoutList?.filter { it.category == "RestDay" } ?: emptyList()
    }


    fun insertWorkoutDayDetails(workout: Workout, dayNumber: Int) {

        val query = """
        INSERT OR REPLACE INTO ${LocalDatabase.TABLE_PROGRAM_WORKOUT} (
            ${COLUMN_WORKOUT_ID},
            ${COLUMN_WORKOUT_NAME},
            ${COLUMN_SETS},
            ${COLUMN_REPS},
            ${LocalDatabase.COLUMN_WEIGHT},
            ${LocalDatabase.COLUMN_COMPLETED_REPS},
            ${COLUMN_WEIGHT_USED},
            ${COLUMN_CATEGORY},
            ${COLUMN_VIDEO_URL},
            ${LocalDatabase.COLUMN_CALORIE_BURNED},
            ${LocalDatabase.COLUMN_DAY_ID},
            ${LocalDatabase.COLUMN_IS_WORKOUT_COMPLETED}
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)
    """.trimIndent()


        val stmt = writableConnection.compileStatement(query)

        stmt.bindLong(1, workout.id.toLong())
        stmt.bindString(2, workout.name)
        stmt.bindLong(3, workout.sets.toLong())
        stmt.bindString(4, workout.reps)
        stmt.bindString(5, workout.weight)
        stmt.bindString(6, "0-0-0")
        stmt.bindString(7, "0.0-0.0-0.0")
        stmt.bindString(8, workout.category)
        stmt.bindString(9, workout.url)
        stmt.bindDouble(10, 0.0.toDouble())
        stmt.bindLong(11, dayNumber.toLong())
        stmt.bindLong(12, 0)


        val rowId = stmt.executeInsert()
        if (rowId == -1L) {
            println("DB_INSERT Failed to insert workout for day $dayNumber: ${workout.name}")
        } else {
            println("DB_INSERT Inserted workout for day $dayNumber: ${workout.name}")
        }
    }

    fun insertProgramWorkout(workout: WorkOutDetail) {

        val query = """
        INSERT OR REPLACE INTO ${LocalDatabase.TABLE_PROGRAM_WORKOUT} (
            ${COLUMN_WORKOUT_ID},
            ${COLUMN_WORKOUT_NAME},
            ${COLUMN_SETS},
            ${COLUMN_REPS},
            ${LocalDatabase.COLUMN_WEIGHT},
            ${LocalDatabase.COLUMN_COMPLETED_REPS},
            ${COLUMN_WEIGHT_USED},
            ${COLUMN_CATEGORY},
            ${COLUMN_VIDEO_URL},
            ${LocalDatabase.COLUMN_CALORIE_BURNED},
            ${LocalDatabase.COLUMN_DAY_ID},
            ${LocalDatabase.COLUMN_IS_WORKOUT_COMPLETED}
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)
    """.trimIndent()

        val stmt = writableConnection.compileStatement(query)

        stmt.bindLong(1, workout.id.toLong())
        stmt.bindString(2, workout.name)
        stmt.bindLong(3, workout.sets.toLong())
        stmt.bindString(4, workout.reps)
        stmt.bindString(5, workout.weight)
        stmt.bindString(6, workout.completedReps)
        stmt.bindString(7, workout.weightUsed)
        stmt.bindString(8, workout.category)
        stmt.bindString(9, workout.url)
        stmt.bindDouble(10, workout.calorieBurned)
        stmt.bindLong(11, workout.dayId.toLong())
        stmt.bindLong(12, workout.isCompleted.toLong())

        val rowId = stmt.executeInsert()
        if (rowId == -1L) {
            println("Initial DB_INSERT Failed to insert workout for day : ${workout.name}")
        } else {
            println("Initial DB_INSERT Inserted workout for day : ${workout.name}")
        }

    }


    fun insertProgramSchedule(schedule: Schedule) {
        val sql = """
        INSERT OR REPLACE INTO ${LocalDatabase.TABLE_PROGRAM_SCHEDULE} (${LocalDatabase.COLUMN_DAY_ID}, ${LocalDatabase.COLUMN_DATE}, ${LocalDatabase.COLUMN_WORKOUT_TYPE})
        VALUES (?, ?, ?)
    """.trimIndent()
        val stmt = writableConnection.compileStatement(sql)
        stmt.bindLong(1, schedule.dayId.toLong())
        stmt.bindString(2, schedule.date)
        stmt.bindString(3, schedule.workoutType)
        val rowId = stmt.executeInsert()
        if (rowId == -1L) {
            println("DB_INSERT Failed to insert program schedule for day ${schedule.dayId}")
        } else {
            println("DB_INSERT  inserted program schedule for day ${schedule.dayId}")
        }
    }

    fun getAllProgramWorkouts(): List<WorkOutDetail> {

        val cursor = readableConnection.rawQuery(
            """
            SELECT * FROM ${LocalDatabase.TABLE_PROGRAM_WORKOUT}
            """.trimIndent(), null
        )
        return convertCursorToWorkouts(cursor)
    }


    fun getProgramWorkOutById(dayId: Int): List<WorkOutDetail> {

        val cursor = readableConnection.rawQuery(
            """
            SELECT * FROM ${LocalDatabase.TABLE_PROGRAM_WORKOUT} WHERE ${LocalDatabase.COLUMN_DAY_ID} = ?
            """.trimIndent(),
            arrayOf(dayId.toString())
        )
        return convertCursorToWorkouts(cursor)
    }


    fun getAllWorkoutsByCategory(workout: WorkOutDetail): List<WorkOutDetail> {


        val cursor = readableConnection.rawQuery(
            """
    SELECT * FROM ${LocalDatabase.TABLE_PROGRAM_WORKOUT}
    WHERE $COLUMN_CATEGORY = ? AND $COLUMN_WORKOUT_ID = ?
      AND ${LocalDatabase.COLUMN_DAY_ID} <= ?
      AND ${LocalDatabase.COLUMN_IS_WORKOUT_COMPLETED} = ?
    ORDER BY ${LocalDatabase.COLUMN_DAY_ID} DESC
    LIMIT 2
    """.trimIndent(),
            arrayOf(workout.category, workout.id.toString(), workout.dayId.toString(), "1")
        )

        return convertCursorToWorkouts(cursor)

    }


    fun convertCursorToWorkouts(cursor: Cursor): List<WorkOutDetail> {
        val workouts = mutableListOf<WorkOutDetail>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_NAME))
                val sets = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETS))
                val reps = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPS))
                val weight =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_WEIGHT))
                val completedReps =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_COMPLETED_REPS))
                val weightUsed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT_USED))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_URL))
                val calorieBurned =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_CALORIE_BURNED))

                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val isCompleted =
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_IS_WORKOUT_COMPLETED))


                val workout = WorkOutDetail(
                    id,
                    name,
                    sets,
                    reps,
                    weight,
                    completedReps,
                    weightUsed,
                    category,
                    url,
                    calorieBurned,
                    dayId,
                    isCompleted
                )

                workouts.add(workout)


            } while (cursor.moveToNext())
        }
        cursor.close()
        return workouts
    }


    fun getProgramSchedule(): List<Schedule> {
        val schedules = mutableListOf<Schedule>()
        val cursor = readableConnection.rawQuery(
            "SELECT * FROM ${LocalDatabase.TABLE_PROGRAM_SCHEDULE}",
            null
        )

        if (cursor.moveToFirst()) {
            do {

                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DATE))
                val workoutType =
                    cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_WORKOUT_TYPE))

                val schedule = Schedule(dayId, date, workoutType)

                schedules.add(schedule)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return schedules
    }

    fun updateWorkoutProgress(
        workoutId: Int,
        dayId: Int,
        completedReps: String,
        weightUsed: String
    ): WorkOutDetail {


        println("$completedReps, $weightUsed")
        val query = """
        UPDATE ${LocalDatabase.TABLE_PROGRAM_WORKOUT} 
        SET ${LocalDatabase.COLUMN_COMPLETED_REPS} = ?, $COLUMN_WEIGHT_USED = ? , ${LocalDatabase.COLUMN_IS_WORKOUT_COMPLETED} = ?
        WHERE $COLUMN_WORKOUT_ID = ? AND ${LocalDatabase.COLUMN_DAY_ID} = ?
    """.trimIndent()

        val stmt = writableConnection.compileStatement(query)
        stmt.bindString(1, completedReps)
        stmt.bindString(2, weightUsed)
        stmt.bindLong(3, 1)
        stmt.bindLong(4, workoutId.toLong())
        stmt.bindLong(5, dayId.toLong())
        stmt.executeUpdateDelete()
        stmt.close()


        val workout = getProgramWorkOutById(dayId).first { it.dayId == dayId && it.id == workoutId }

        println("updateWorkoutProgress is called : $workout")


        return workout

    }


    fun getAllWorkoutLog(): List<WorkoutLog> {
        val workoutLogs = mutableListOf<WorkoutLog>()

        val cursor = readableConnection.rawQuery(
            "SELECT DISTINCT * FROM ${LocalDatabase.TABLE_WORKOUT_LOG}",
            null
        )

        if (cursor.moveToFirst()) {
            do {

                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DATE))

                val workoutLog = WorkoutLog(dayId, date)

                println(workoutLog)
                workoutLogs.add(workoutLog)

            } while (cursor.moveToNext())
        } else {
            println("cursor is empty")
        }

        cursor.close()

        return workoutLogs
    }

    fun saveDailyStrike(dayNumber: Int, date: String) {
        val query = """
            INSERT OR REPLACE INTO ${LocalDatabase.TABLE_WORKOUT_LOG} (${LocalDatabase.COLUMN_DAY_ID},${LocalDatabase.COLUMN_DATE})
            VALUES (?,?)
        """.trimIndent()

        val stmt = writableConnection.compileStatement(query)
        stmt.bindLong(1, dayNumber.toLong())
        stmt.bindString(2, date)

        val rowId = stmt.executeInsert()
        if (rowId == -1L) {
            println("failed to insert Streak for day $dayNumber with $rowId")
        } else {
            println(" inserted Streak for day $dayNumber")
        }

    }

    fun insertWorkoutLog(log: WorkoutLog) {
        val query = """
            INSERT OR REPLACE INTO ${LocalDatabase.TABLE_WORKOUT_LOG} (${LocalDatabase.COLUMN_DAY_ID},${LocalDatabase.COLUMN_DATE})
            VALUES (?,?)
        """.trimIndent()

        val stmt = writableConnection.compileStatement(query)
        stmt.bindLong(1, log.dayId.toLong())
        stmt.bindString(2, log.date)

        val rowId = stmt.executeInsert()
        if (rowId == -1L) {
            println("insert initial workout log  for day ${log.dayId} with $rowId")
        } else {
            println(" failed in insert initial workout log  for day ${log.dayId}")
        }
    }

    fun getDayIdByDate(date: String): Int? {


        val query =
            "SELECT $COLUMN_DAY_ID FROM ${LocalDatabase.TABLE_PROGRAM_SCHEDULE} WHERE $COLUMN_DATE = ? LIMIT 1"
        readableConnection.rawQuery(query, arrayOf(date)).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_ID))
            }
        }
        return null
    }


    fun getAllWorkoutLogsForProgressData(dayId: Int): List<WorkoutLog> {

        val workoutLogs = mutableListOf<WorkoutLog>()

        val query = """
            SELECT DISTINCT * FROM ${LocalDatabase.TABLE_WORKOUT_LOG} WHERE $COLUMN_DAY_ID <= ?
        """.trimIndent()
        val cursor = readableConnection.rawQuery(
            query,
            arrayOf(dayId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val dayId = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DAY_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDatabase.COLUMN_DATE))
                workoutLogs.add(WorkoutLog(dayId, date))
            } while (cursor.moveToNext())
        } else {
            println("cursor is empty")
        }
        cursor.close()
        return workoutLogs
    }


}
