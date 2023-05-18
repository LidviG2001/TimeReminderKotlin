package com.example.timereminderkotlin

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationListener : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val time = intent?.getStringExtra("key")
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "id"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_MAX
            )
            notificationChannel.description = "Sample Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time to smoke")
            .setContentText("You can smoke: $time minutes")
            .setContentInfo("Information")
        notificationManager
            .notify(1, notificationBuilder.build())

        Log.d("TAG", "Notification is worked")
    }
}