package com.myth.diaryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.myth.diaryapp.model.DiaryRecord
import com.myth.diaryapp.repository.RecordRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordViewModel(
    app: Application, private val recordRepository: RecordRepository
) : AndroidViewModel(app) {
    fun addRecord(record: DiaryRecord) = viewModelScope.launch {
        recordRepository.insertRecord(record)
    }

    fun deleteRecord(record: DiaryRecord) = viewModelScope.launch {
        recordRepository.deleteRecord(record)
    }

    fun updateRecord(record: DiaryRecord) = viewModelScope.launch {
        recordRepository.updateRecord(record)
    }

    fun getAllRecords() = recordRepository.getAllRecords()
    fun searchRecords(query: String) = recordRepository.searchRecords(query)
    fun getRecordsByDate(date: String) = recordRepository.getRecordsByDate(date)
    fun getRecordsByFormattedDate(date: Date): LiveData<List<DiaryRecord>> {
        val dateFormat = SimpleDateFormat("HHmm EEEE d MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        return recordRepository.getRecordsByDate(formattedDate)
    }

}