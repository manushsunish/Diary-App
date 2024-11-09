package com.myth.diaryapp.repository

import com.myth.diaryapp.database.RecordDatabase
import com.myth.diaryapp.model.DiaryRecord

class RecordRepository(private val db: RecordDatabase) {

    suspend fun insertRecord(record: DiaryRecord) = db.getRecordDao().insertRecord(record)
    suspend fun updateRecord(record: DiaryRecord) = db.getRecordDao().updateRecord(record)
    suspend fun deleteRecord(record: DiaryRecord) = db.getRecordDao().deleteRecord(record)


    fun getAllRecords() = db.getRecordDao().getAllRecords()
    fun searchRecords(query: String) = db.getRecordDao().searchRecord(query)
    fun getRecordsByDate(date: String) = db.getRecordDao().getRecordsByDate(date)

}