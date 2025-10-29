package com.yadhuChoudhary.MyRuns2

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ManualActivity : AppCompatActivity() {

    private var selectedDate: Long = 0L
    private var selectedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_activity)

        val tvDate: TextView = findViewById(R.id.tv_date)
        val tvTime: TextView = findViewById(R.id.tv_time)
        val tvDuration: TextView = findViewById(R.id.tv_duration)
        val tvDistance: TextView = findViewById(R.id.tv_distance)
        val tvCalories: TextView = findViewById(R.id.tv_calories)
        val tvHeartRate: TextView = findViewById(R.id.tv_heart_rate)
        val tvComment: TextView = findViewById(R.id.tv_comment)
        val btnSave: Button = findViewById(R.id.btn_save)
        val btnCancel: Button = findViewById(R.id.btn_cancel)

        tvDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                selectedDate = cal.timeInMillis
                tvDate.text = "Date: $day/${month+1}/$year"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        tvTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                selectedTime = cal.timeInMillis
                tvTime.text = "Time: $hour:$minute"
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        tvDuration.setOnClickListener { showNumberInputDialog("Duration") { tvDuration.text = "Duration: $it" } }
        tvDistance.setOnClickListener { showNumberInputDialog("Distance") { tvDistance.text = "Distance: $it" } }
        tvCalories.setOnClickListener { showNumberInputDialog("Calories") { tvCalories.text = "Calories: $it" } }
        tvHeartRate.setOnClickListener { showNumberInputDialog("Heart Rate") { tvHeartRate.text = "Heart Rate: $it" } }
        tvComment.setOnClickListener { showTextInputDialog("Comment") { tvComment.text = "Comment: $it" } }

        btnSave.setOnClickListener {
            Toast.makeText(this, "Saved entry", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showNumberInputDialog(title: String, onResult: (String) -> Unit) {
        val editText = EditText(this).apply { inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { _, _ -> onResult(editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showTextInputDialog(title: String, onResult: (String) -> Unit) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { _, _ -> onResult(editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
