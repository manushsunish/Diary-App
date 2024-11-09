package com.myth.diaryapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myth.diaryapp.model.DiaryRecord

@Database(entities = [DiaryRecord::class], version = 4)
abstract class RecordDatabase : RoomDatabase() {

    abstract fun getRecordDao(): RecordDAO


    companion object {
        @Volatile
        private var instance: RecordDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
            context.applicationContext,
            RecordDatabase::class.java,
            "record_d"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
}