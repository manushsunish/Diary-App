package com.myth.diaryapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myth.diaryapp.repository.RecordRepository

class RecordViewModelFactory(
    val application: Application,
    private val recordRepository: RecordRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordViewModel(application, recordRepository) as T
    }
}