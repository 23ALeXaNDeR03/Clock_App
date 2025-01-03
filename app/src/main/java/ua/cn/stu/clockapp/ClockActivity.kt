package ua.cn.stu.clockapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ClockActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var timezoneTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var timerButton: Button
    private lateinit var settingsButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateFormatFull = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timezoneFormat = SimpleDateFormat("zzz", Locale.getDefault())

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateClockDisplay()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        timeTextView = findViewById(R.id.time_text_view)
        dateTextView = findViewById(R.id.date_text_view)
        timezoneTextView = findViewById(R.id.timezone_text_view)
        yearTextView = findViewById(R.id.year_text_view)
        timerButton = findViewById(R.id.timer_button)
        settingsButton = findViewById(R.id.settings_button)

        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        timerButton.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        startClock()
    }

    private fun updateClockDisplay() {
        val savedTimezone = sharedPreferences.getString("timezone", TimeZone.getDefault().id) ?: TimeZone.getDefault().id
        val timezone = TimeZone.getTimeZone(savedTimezone)

        if (timezone.id == "GMT" && savedTimezone != "GMT") {
            timezone.setID(TimeZone.getDefault().id)
        }

        val calendar = Calendar.getInstance(timezone)

        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormatFull = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val timezoneOffset = timezone.rawOffset / 3600000 // Зміщення в годинах
        val timezoneDisplay = if (timezoneOffset >= 0) {
            "GMT+$timezoneOffset:00"
        } else {
            "GMT$timezoneOffset:00"
        }

        dateFormat.timeZone = timezone
        dateFormatFull.timeZone = timezone

        val currentTime = dateFormat.format(calendar.time)
        val currentDate = dateFormatFull.format(calendar.time)
        val currentYear = calendar.get(Calendar.YEAR)

        runOnUiThread {
            timeTextView.text = currentTime
            dateTextView.text = currentDate
            timezoneTextView.text = "Time Zone: $timezoneDisplay"
            yearTextView.text = "Year: $currentYear"
        }
    }

    private fun startClock() {
        handler.post(updateRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(updateRunnable)
        startClock()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}