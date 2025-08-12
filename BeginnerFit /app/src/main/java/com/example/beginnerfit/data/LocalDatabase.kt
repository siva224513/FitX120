package com.example.beginnerfit.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.beginnerfit.model.User

class LocalDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        private const val DATABASE_NAME = "BeginnerFit.db"
        private const val DATABASE_VERSION = 1

        //user table
        const val TABLE_USER = "user"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AGE = "age"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_HEIGHT = "height"

        const val COLUMN_START_WEIGHT = "start_weight"
        const val COLUMN_CURRENT_WEIGHT = "current_weight"
        const val COLUMN_TARGET_WEIGHT = "target_weight"
        const val COLUMN_MAINTENANCE_CALORIE = "daily_maintenance_calorie"
        const val COLUMN_PROGRAM_TYPE = "program_plan"
        const val COLUMN_PROGRAM_START_DATE = "program_start_date"


        //workout table
        const val TABLE_WORKOUTS = "workouts"


        //food table
        const val TABLE_FOOD = "food"
        const val COLUMN_FOOD_ID = "food_id"
        const val COLUMN_FOOD_NAME = "food_name"
        const val COLUMN_FOOD_CALORIE = "food_calorie"
        const val COLUMN_FOOD_PROTEIN = "protein"
        const val COLUMN_FOOD_CARBS = "carbs"
        const val COLUMN_FOOD_FIBRE = "fibre"
        const val COLUMN_FOOD_FAT = "fat"

        //food log table
        const val TABLE_FOOD_LOG = "food_log"
        const val COLUMN_FOOD_LOG_ID = "food_log_id"
        const val COLUMN_MEAL_TYPE = "meal_type"


        //workout log table
        const val TABLE_WORKOUT_LOG = "workout_log"


        //workout_day_details
        const val TABLE_WORKOUT_DAY_DETAILS = "workout_day_details"
        const val COLUMN_DAY_NAME = "day_name"


        //water
        const val TABLE_WATER = "water_log"
        const val COLUMN_GLASS_COUNT = "glass_count"

        //weight
        const val TABLE_WEIGHT = "weight_log"
        const val COLUMN_WEIGHT_COUNT = "weight"

        //sleep
        const val TABLE_SLEEP = "sleep_log"
        const val COLUMN_SLEEP_ACHIEVED = "sleep_achieved"


        //program-schedule
        const val TABLE_PROGRAM_SCHEDULE = "program_schedule"
        const val COLUMN_DAY_ID = "day_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_WORKOUT_TYPE = "workout_type"


        //program workout table
        const val TABLE_PROGRAM_WORKOUT = "program_workout"
        const val COLUMN_WORKOUT_ID = "workout_id"
        const val COLUMN_WORKOUT_NAME = "workout_name"
        const val COLUMN_SETS = "sets"
        const val COLUMN_REPS = "reps"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_COMPLETED_REPS = "completed_reps"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_VIDEO_URL = "video_url"
        const val COLUMN_CALORIE_BURNED = "calorie_burned"
        const val COLUMN_WEIGHT_USED = "weight_used"
        const val COLUMN_IS_WORKOUT_COMPLETED = "is_workout_completed"


        const val USER_TABLE_QUERY = """
            CREATE TABLE $TABLE_USER(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_AGE INTEGER,
            $COLUMN_GENDER TEXT,
            $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
            $COLUMN_PASSWORD TEXT NOT NULL,
            $COLUMN_HEIGHT REAL,
            $COLUMN_START_WEIGHT REAL,
            $COLUMN_CURRENT_WEIGHT REAL,
            $COLUMN_TARGET_WEIGHT REAL,
            $COLUMN_MAINTENANCE_CALORIE INTEGER,
            $COLUMN_PROGRAM_TYPE TEXT,
            $COLUMN_PROGRAM_START_DATE TEXT
            )
        """

        const val PROGRAM_WORKOUT_TABLE_QUERY = """
            CREATE TABLE $TABLE_PROGRAM_WORKOUT(
            $COLUMN_WORKOUT_ID INTEGER NOT NULL,
            $COLUMN_WORKOUT_NAME TEXT NOT NULL,
            $COLUMN_SETS INTEGER NOT NULL,
            $COLUMN_REPS TEXT NOT NULL,
            $COLUMN_WEIGHT TEXT NOT NULL,
            $COLUMN_COMPLETED_REPS TEXT NOT NULL,
            $COLUMN_WEIGHT_USED TEXT NOT NULL,
            $COLUMN_CATEGORY TEXT NOT NULL,
            $COLUMN_VIDEO_URL TEXT NOT NULL,
            $COLUMN_CALORIE_BURNED REAL NOT NULL,
            $COLUMN_DAY_ID INTEGER NOT NULL,
            $COLUMN_IS_WORKOUT_COMPLETED INTEGER NOT NULL,
            PRIMARY KEY($COLUMN_WORKOUT_ID,$COLUMN_DAY_ID),
            FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES  $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
            )
        """


        const val TABLE_PROGRAM_SCHEDULE_QUERY = """
            CREATE TABLE $TABLE_PROGRAM_SCHEDULE(
              $COLUMN_DAY_ID INTEGER PRIMARY KEY,
              $COLUMN_DATE TEXT NOT NULL,
              $COLUMN_WORKOUT_TYPE TEXT NOT NULL
            )
        """

        const val TABLE_WORKOUT_LOG_QUERY = """
            CREATE TABLE $TABLE_WORKOUT_LOG(
              $COLUMN_DATE TEXT NOT NULL,
              $COLUMN_DAY_ID INTEGER NOT NULL,
              FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_WORKOUT($COLUMN_DAY_ID),
              PRIMARY KEY ($COLUMN_DATE, $COLUMN_DAY_ID)
            )
        """

        const val TABLE_FOOD_QUERY = """
            CREATE TABLE $TABLE_FOOD(
              $COLUMN_FOOD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
              $COLUMN_FOOD_NAME TEXT NOT NULL,
              $COLUMN_FOOD_CALORIE INTEGER NOT NULL,           
              $COLUMN_FOOD_CARBS REAL NOT NULL,
              $COLUMN_FOOD_PROTEIN REAL NOT NULL,
              $COLUMN_FOOD_FAT REAL NOT NULL,
              $COLUMN_FOOD_FIBRE REAL NOT NULL
            )
        """


        const val TABLE_FOOD_LOG_QUERY = """
            CREATE TABLE $TABLE_FOOD_LOG(
               $COLUMN_FOOD_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
               $COLUMN_DATE TEXT NOT NULL,
               $COLUMN_MEAL_TYPE TEXT NOT NULL,
               $COLUMN_FOOD_ID INTEGER NOT NULL,
               $COLUMN_DAY_ID INTEGER NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID),
               FOREIGN KEY ($COLUMN_FOOD_ID) REFERENCES $TABLE_FOOD($COLUMN_FOOD_ID)
            )
        """

        const val TABLE_SLEEP_QUERY = """
             CREATE TABLE $TABLE_SLEEP(
               $COLUMN_DAY_ID INTEGER PRIMARY KEY,
               $COLUMN_SLEEP_ACHIEVED BOOLEAN NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
          )
        """

        const val TABLE_WEIGHT_QUERY = """
               CREATE TABLE $TABLE_WEIGHT(
               $COLUMN_DAY_ID INTEGER PRIMARY KEY,             
               $COLUMN_WEIGHT_COUNT REAL NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
              )
        """

        const val TABLE_WATER_QUERY = """
            CREATE TABLE $TABLE_WATER(
               $COLUMN_DAY_ID INTEGER PRIMARY KEY,
               $COLUMN_GLASS_COUNT INTEGER NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
            )
        """

        private var instance: LocalDatabase? = null
        fun getInstance(context: Context): LocalDatabase {
            if (instance == null) {
                return LocalDatabase(context)
            }
            return instance!!
        }

    }


    override fun onCreate(db: SQLiteDatabase?) {
        println("LocalDatabase onCreate Called...")


        db?.execSQL(USER_TABLE_QUERY)

        db?.execSQL(PROGRAM_WORKOUT_TABLE_QUERY)
        db?.execSQL(TABLE_PROGRAM_SCHEDULE_QUERY)
        db?.execSQL(TABLE_WORKOUT_LOG_QUERY)

        db?.execSQL(TABLE_FOOD_QUERY)
        db?.execSQL(TABLE_FOOD_LOG_QUERY)

        db?.execSQL(TABLE_SLEEP_QUERY)
        db?.execSQL(TABLE_WEIGHT_QUERY)
        db?.execSQL(TABLE_WATER_QUERY)

        val tablesToCheck = listOf(
            TABLE_PROGRAM_SCHEDULE,
            TABLE_PROGRAM_WORKOUT,
            TABLE_FOOD_LOG,
            TABLE_WORKOUT_LOG,
            TABLE_SLEEP,
            TABLE_WATER,
            TABLE_WEIGHT,
            TABLE_FOOD,
            TABLE_USER
        )

        for (table in tablesToCheck) {
            val exists = isTableExists(db!!, table)
            println("TABLE_CHECK $table exists: $exists")
        }
    }

    fun isTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    private fun createFoodLogTable(): String {
        println("food log table created successfully")
        return """
            CREATE TABLE $TABLE_FOOD_LOG(
               $COLUMN_FOOD_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
               $COLUMN_DATE TEXT NOT NULL,
               $COLUMN_MEAL_TYPE TEXT NOT NULL,
               $COLUMN_FOOD_ID INTEGER NOT NULL,
               $COLUMN_DAY_ID INTEGER NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID),
               FOREIGN KEY ($COLUMN_FOOD_ID) REFERENCES $TABLE_FOOD($COLUMN_FOOD_ID)
            )
        """.trimIndent()
    }

    private fun createWorkoutLogTable(): String {
        return """
            CREATE TABLE $TABLE_WORKOUT_LOG(
              $COLUMN_DATE TEXT NOT NULL,
              $COLUMN_DAY_ID INTEGER NOT NULL,
              FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_WORKOUT($COLUMN_DAY_ID),
              PRIMARY KEY ($COLUMN_DATE, $COLUMN_DAY_ID)
            )
        """.trimIndent()
    }


    private fun createProgramWorkoutTable(): String {
        return """
            CREATE TABLE $TABLE_PROGRAM_WORKOUT(
            $COLUMN_WORKOUT_ID INTEGER NOT NULL,
            $COLUMN_WORKOUT_NAME TEXT NOT NULL,
            $COLUMN_SETS INTEGER NOT NULL,
            $COLUMN_REPS TEXT NOT NULL,
            $COLUMN_WEIGHT TEXT NOT NULL,
            $COLUMN_COMPLETED_REPS TEXT NOT NULL,
            $COLUMN_WEIGHT_USED TEXT NOT NULL,
            $COLUMN_CATEGORY TEXT NOT NULL,
            $COLUMN_VIDEO_URL TEXT NOT NULL,
            $COLUMN_CALORIE_BURNED REAL NOT NULL,
            $COLUMN_DAY_ID INTEGER NOT NULL,
            $COLUMN_IS_WORKOUT_COMPLETED INTEGER NOT NULL,
            PRIMARY KEY($COLUMN_WORKOUT_ID,$COLUMN_DAY_ID),
            FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES  $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
            )
        """.trimIndent()
    }


    private fun createProgramScheduleTable(): String {
        return """
            CREATE TABLE $TABLE_PROGRAM_SCHEDULE(
              $COLUMN_DAY_ID INTEGER PRIMARY KEY,
              $COLUMN_DATE TEXT NOT NULL,
              $COLUMN_WORKOUT_TYPE TEXT NOT NULL
            )
        """.trimIndent()

    }

    private fun createSleepTable(): String {
        println("sleep table created successfully")
        return """
             CREATE TABLE $TABLE_SLEEP(
               $COLUMN_DAY_ID INTEGER PRIMARY KEY,
               $COLUMN_SLEEP_ACHIEVED BOOLEAN NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
          )
        """.trimIndent()
    }

    private fun createWaterTable(): String {
        println("water table created successfully")
        return """
            CREATE TABLE $TABLE_WATER(
               $COLUMN_DAY_ID INTEGER PRIMARY KEY,
               $COLUMN_GLASS_COUNT INTEGER NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
            )
        """.trimIndent()
    }


    private fun createWeightTable(): String {
        println("weight table created successfully")
        return """
               CREATE TABLE $TABLE_WEIGHT(
               $COLUMN_DAY_ID INTEGER PRIMARY KEY,             
               $COLUMN_WEIGHT_COUNT REAL NOT NULL,
               FOREIGN KEY ($COLUMN_DAY_ID) REFERENCES $TABLE_PROGRAM_SCHEDULE($COLUMN_DAY_ID)
              )
        """.trimIndent()
    }


    private fun createUserTable(): String {
        println("user table created successfully")
        return """
            CREATE TABLE $TABLE_USER(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_AGE INTEGER,
            $COLUMN_GENDER TEXT,
            $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
            $COLUMN_PASSWORD TEXT NOT NULL,
            $COLUMN_HEIGHT REAL,
            $COLUMN_START_WEIGHT REAL,
            $COLUMN_CURRENT_WEIGHT REAL,
            $COLUMN_TARGET_WEIGHT REAL,
            $COLUMN_MAINTENANCE_CALORIE INTEGER,
            $COLUMN_PROGRAM_TYPE TEXT,
            $COLUMN_PROGRAM_START_DATE TEXT
            )
        """.trimIndent()
    }


    private fun createFoodTable(): String {
        println("food table created successfully")
        return """
            CREATE TABLE $TABLE_FOOD(
              $COLUMN_FOOD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
              $COLUMN_FOOD_NAME TEXT NOT NULL,
              $COLUMN_FOOD_CALORIE INTEGER NOT NULL,           
              $COLUMN_FOOD_CARBS REAL NOT NULL,
              $COLUMN_FOOD_PROTEIN REAL NOT NULL,
              $COLUMN_FOOD_FAT REAL NOT NULL,
              $COLUMN_FOOD_FIBRE REAL NOT NULL
            )
        """.trimIndent()
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        println("LocalDatabase onUpgrade Called...")
    }


}