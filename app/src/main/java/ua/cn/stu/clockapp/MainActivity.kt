package ua.cn.stu.clockapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val duration = 2000
        val updateInterval = 20
        val maxProgress = 100
        var currentProgress = 0

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (currentProgress < maxProgress) {
                    currentProgress += (updateInterval * maxProgress) / duration
                    progressBar.progress = currentProgress
                    handler.postDelayed(this, updateInterval.toLong())
                } else {

                    val intent = Intent(this@MainActivity, ClockActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        handler.post(runnable)
    }
}
