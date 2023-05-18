package com.example.timereminderkotlin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.google.android.material.shadow.ShadowRenderer
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.platform.MaterialArcMotion
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Mainfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Mainfragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mainfragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val start_btn = view.findViewById<Button>(R.id.start_btn)
        val stop_btn = view.findViewById<Button>(R.id.stop_btn)

        val start = view.findViewById<Button>(R.id.start)
        val stop = view.findViewById<Button>(R.id.stop)

        val interval = view.findViewById<EditText>(R.id.interval_et)
        val smoke_et = view.findViewById<EditText>(R.id.smoke_et)

        val start_tv = view.findViewById<TextView>(R.id.start_tv)
        val stop_tv = view.findViewById<TextView>(R.id.stop_tv)

        val SAVED_START = "saved start"
        val SAVED_STOP = "saved stop"
        val SAVED_INTERVAL = "saved interval"

        val calendarStart = Calendar.getInstance()
        val calendarStop = Calendar.getInstance()

        start_tv.setText("Starting time is ${convertTime(extract(SAVED_START), "HH:mm")}")
        stop_tv.setText("Finishing time is ${convertTime(extract(SAVED_STOP), "HH:mm")}")
        interval.setText(extract(SAVED_INTERVAL).toString())

        start_btn.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Test1")
                .build()

            picker.addOnPositiveButtonClickListener {

                calendarStart.set(Calendar.MINUTE, picker.minute)
                calendarStart.set(Calendar.HOUR_OF_DAY, picker.hour)

                start_tv.setText("Start time is ${convertTime(calendarStart.timeInMillis, "HH:mm")}")

                save(SAVED_START, calendarStart.timeInMillis)
            }

            picker.show(childFragmentManager, "start_picker")
        }

        stop_btn.setOnClickListener{
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Test2")
                .build()

            picker.addOnPositiveButtonClickListener {

                calendarStop.set(Calendar.MINUTE, picker.minute)
                calendarStop.set(Calendar.HOUR_OF_DAY, picker.hour)

                stop_tv.setText("Start time is ${convertTime(calendarStop.timeInMillis, "HH:mm")}")

                save(SAVED_STOP, calendarStop.timeInMillis)
            }

            picker.show(childFragmentManager, "stop_picker")
        }

        start.setOnClickListener {
            if(interval.text.length == 0){
                Toast.makeText(context, "Set interval!", Toast.LENGTH_LONG).show()
            }

            val value = Integer.parseInt(interval.text.toString())

            if(value <= 0) {val value = 1}

            save(SAVED_INTERVAL, value.toLong())

            val intent = Intent(activity, NotificationListener::class.java)
            intent.putExtra("key", smoke_et.text.toString())

            val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendarStart.timeInMillis,
                1000L* 60 * value,
                pendingIntent)

            val cancelIntent = Intent(activity, CancelAlarmBroadcastReceiver::class.java)
            cancelIntent.putExtra("key", pendingIntent)

            val cancelPendingIntent = PendingIntent.getBroadcast(activity, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarStop.timeInMillis, cancelPendingIntent)

            Log.d("TAG", "Alarm is setted")
        }

        stop.setOnClickListener {
            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val cancelIntent = Intent(activity, CancelAlarmBroadcastReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(activity, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

            cancelIntent.putExtra("key", pendingIntent)

            val cancelPendingIntent = PendingIntent.getBroadcast(activity, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarStop.timeInMillis, cancelPendingIntent)

            Log.d("TAG", "Alarm is stopped")
        }
    }

    public fun convertTime(time : Long, pattern : String): String? {
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(time)
    }

    public fun save(key : String, time : Long){
        val sPref : SharedPreferences? = activity?.getPreferences(MODE_PRIVATE)
        val ed : SharedPreferences.Editor? = sPref?.edit()
        ed?.putLong(key, time)
        ed?.apply()
    }

    public fun extract(key : String): Long {
        val sPref : SharedPreferences? = activity?.getPreferences(MODE_PRIVATE)
        return sPref!!.getLong(key, 0L)
    }
}

