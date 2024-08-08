package com.smg.kasirsmg

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.smg.kasirsmg.model.Pesanan
import kotlin.random.Random


class NotificationService (
    private val context: Context,
    private val transaksi: Pesanan
) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showNotification(){
        val notification = NotificationCompat.Builder(context, "transaction_accepted")
            .setContentTitle("Ada Pesanan Baru")
            .setContentText("Pesanan dari ${transaksi.namaPengguna}")
            .setSmallIcon(R.drawable.cart_fill)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }
}