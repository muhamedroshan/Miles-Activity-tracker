package com.example.miles

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.miles.data.ItemRoomDatabase



class LocationApp: Application() {
    val appViewModel = ViewModelProvider( ViewModelStore() ,ViewModelProvider.AndroidViewModelFactory(this)).get(ceikliViewModel::class.java)
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}