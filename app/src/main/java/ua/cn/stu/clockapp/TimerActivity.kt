package ua.cn.stu.clockapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var pauseButton: Button
    private lateinit var lapButton: Button
    private lateinit var lapsListView: ListView

    private val handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isRunning = false
    private var laps: MutableList<String> = mutableListOf()
    private lateinit var lapsAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            title = "Timer"
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        timerTextView = findViewById(R.id.timer_text_view)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        pauseButton = findViewById(R.id.pause_button)
        lapButton = findViewById(R.id.lap_button)
        lapsListView = findViewById(R.id.laps_list_view)

        lapsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, laps).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        lapsListView.adapter = lapsAdapter
        lapsListView.setBackgroundColor(Color.WHITE)

        setupButtonLogic()
    }

    private fun setupButtonLogic() {
        startButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                startTime = System.currentTimeMillis() - elapsedTime
                handler.post(updateTimerRunnable)
                toggleButtons(true)
            }
        }

        stopButton.setOnClickListener {
            if (elapsedTime > 0) {
                handler.removeCallbacks(updateTimerRunnable)
                isRunning = false
                elapsedTime = 0
                laps.clear()
                lapsAdapter.notifyDataSetChanged()
                timerTextView.text = formatTime(0)
                toggleButtons(false)
            }
        }

        pauseButton.setOnClickListener {
            if (isRunning) {
                handler.removeCallbacks(updateTimerRunnable)
                isRunning = false
                pauseButton.text = "Resume"
            } else {
                isRunning = true
                startTime = System.currentTimeMillis() - elapsedTime
                handler.post(updateTimerRunnable)
                pauseButton.text = "Pause"
            }
            toggleButtons(isRunning)
        }

        lapButton.setOnClickListener {
            if (isRunning) {
                val lapTime = elapsedTime - laps.sumOf { parseTime(it) }
                laps.add(formatTime(lapTime))
                lapsAdapter.notifyDataSetChanged()
            }
        }

        toggleButtons(false)
    }

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            elapsedTime = System.currentTimeMillis() - startTime
            timerTextView.text = formatTime(elapsedTime)
            handler.postDelayed(this, 100)
        }
    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        val milliseconds = (timeInMillis % 1000) / 100
        return String.format("%02d:%02d.%01d", minutes, seconds, milliseconds)
    }

    private fun parseTime(time: String): Long {
        val parts = time.split(":", ".")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        val milliseconds = parts[2].toLong() * 100
        return (minutes * 60 + seconds) * 1000 + milliseconds
    }

    private fun toggleButtons(timerRunning: Boolean) {
        startButton.isEnabled = !timerRunning
        pauseButton.isEnabled = timerRunning
        lapButton.isEnabled = timerRunning
        stopButton.isEnabled = elapsedTime > 0

        pauseButton.text = if (timerRunning) "Pause" else "Resume"
    }
}