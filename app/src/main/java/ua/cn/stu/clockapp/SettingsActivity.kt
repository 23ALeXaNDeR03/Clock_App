package ua.cn.stu.clockapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.Toolbar
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var timezoneSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Settings"
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        timezoneSpinner = findViewById(R.id.timezone_spinner)
        saveButton = findViewById(R.id.save_button)

        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        val timeZones = TimeZone.getAvailableIDs().sortedBy { it }

        val adapter = ArrayAdapter(
            this,
            R.layout.custom_spinner_selected_item, // Для вибраного елемента
            timeZones
        ).apply {
            setDropDownViewResource(R.layout.custom_spinner_item) // Для випадаючого списку
        }
        timezoneSpinner.adapter = adapter

        try {
            val popupField = Spinner::class.java.getDeclaredField("mPopup")
            popupField.isAccessible = true
            val popupWindow = popupField.get(timezoneSpinner) as ListPopupWindow

            popupWindow.height = 500

            popupWindow.verticalOffset = -timezoneSpinner.height // Піднімаємо список до Spinner
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val currentTimezone = sharedPreferences.getString("timezone", TimeZone.getDefault().id)
        val currentIndex = timeZones.indexOf(currentTimezone)
        if (currentIndex != -1) {
            timezoneSpinner.setSelection(currentIndex)
        }

        saveButton.setOnClickListener {
            val selectedTimezone = timezoneSpinner.selectedItem.toString()

            val editor = sharedPreferences.edit()
            editor.putString("timezone", selectedTimezone)
            editor.apply()

            Toast.makeText(this, "Time zone saved: $selectedTimezone", Toast.LENGTH_SHORT).show()

            finish()
        }
    }
}
