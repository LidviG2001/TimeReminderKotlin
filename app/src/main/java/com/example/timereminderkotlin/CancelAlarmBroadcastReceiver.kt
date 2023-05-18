package com.example.timereminderkotlin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CancelAlarmBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingIntent = intent?.getParcelableExtra<PendingIntent>("key")
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}