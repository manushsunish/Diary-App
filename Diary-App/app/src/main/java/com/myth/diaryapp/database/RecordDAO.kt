package com.myth.diaryapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.myth.diaryapp.model.DiaryRecord

@Dao
interface RecordDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DiaryRecord)

    @Delete
    suspend fun deleteRecord(record: DiaryRecord)

    @Update
    suspend fun updateRecord(record: DiaryRecord)

    @Query("SELECT * FROM records ORDER by recordTimestamp DESC")
    fun getAllRecords(): LiveData<List<DiaryRecord>>

    @Query("SELECT * FROM records WHERE recordTitle LIKE '%' || :query || '%' OR recordBody LIKE '%' || :query || '%'")
    fun searchRecord(query: String): LiveData<List<DiaryRecord>>

    @Query("SELECT * FROM records WHERE recordTimestamp LIKE '%' || :date || '%'")
    fun getRecordsByDate(date: String): LiveData<List<DiaryRecord>>


}