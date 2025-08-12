package com.example.beginnerfit.data

import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_DATE
import com.example.beginnerfit.data.LocalDatabase.Companion.COLUMN_DAY_ID
import com.example.beginnerfit.data.LocalDatabase.Companion.TABLE_PROGRAM_SCHEDULE

class ProfileDao(val databaseConnection: LocalDatabase) {

    fun getAllProgramDates(): List<String> {
        val db =databaseConnection.readableDatabase
        val dates = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT $COLUMN_DATE FROM $TABLE_PROGRAM_SCHEDULE ORDER BY $COLUMN_DAY_ID", null)
        if (cursor.moveToFirst()) {
            do {
                dates.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dates
    }

}