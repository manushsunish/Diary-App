package com.myth.diaryapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.myth.diaryapp.database.RecordDatabase
import com.myth.diaryapp.repository.RecordRepository
import com.myth.diaryapp.viewmodel.RecordViewModel
import com.myth.diaryapp.viewmodel.RecordViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var recordViewModel: RecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViewModel()
    }
    private fun setUpViewModel(){
        val recordRepository = RecordRepository(RecordDatabase(this))

        val recordViewModelFactory = RecordViewModelFactory(application, recordRepository)

        recordViewModel = ViewModelProvider(
            this, recordViewModelFactory
        )[RecordViewModel::class.java]
    }

    // In MainActivity or Application class
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("reminderChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}


/*


Diary journal that stores the daily account along with media
The journals will be stored based on the date and can be searched using date
Reminders can also be set on a daily basis at set time

 */