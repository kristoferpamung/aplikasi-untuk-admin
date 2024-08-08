package com.smg.kasirsmg

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        val notificationChannel = NotificationChannel(
            "transaction_accepted",
            "Transaksi Berhasil",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.description = "Notifikasi untuk transaksi berhasil"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel)
    }
}