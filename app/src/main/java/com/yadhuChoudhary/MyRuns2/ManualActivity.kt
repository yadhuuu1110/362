package com.yadhuChoudhary.MyRuns2

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ManualActivity : AppCompatActivity() {

    private lateinit var repository: ExerciseRepository
    private var currentEntry: ExerciseEntry = ExerciseEntry()
    private val dateFormat = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_activity)

        // Initialize database
        val database = ExerciseDatabase.getDatabase(applicationContext)
        repository = ExerciseRepository(database.exerciseDao())

        val tvDate: TextView = findViewById(R.id.tv_date)
        val tvTime: TextView = findViewById(R.id.tv_time)
        val tvDuration: TextView = findViewById(R.id.tv_duration)
        val tvDistance: TextView = findViewById(R.id.tv_distance)
        val tvCalories: TextView = findViewById(R.id.tv_calories)
        val tvHeartRate: TextView = findViewById(R.id.tv_heart_rate)
        val tvComment: TextView = findViewById(R.id.tv_comment)
        val btnSave: Button = findViewById(R.id.btn_save)
        val btnCancel: Button = findViewById(R.id.btn_cancel)

        // Get input type and activity type from intent
        val inputType = intent.getIntExtra(Constants.EXTRA_INPUT_TYPE, Constants.INPUT_TYPE_MANUAL)
        val activityType = intent.getIntExtra(Constants.EXTRA_ACTIVITY_TYPE, 0)

        currentEntry.inputType = inputType
        currentEntry.activityType = activityType
        currentEntry.dateTime = Calendar.getInstance()

        // Set initial display - just labels
        tvDate.text = "Date"
        tvTime.text = "Time"
        tvDuration.text = "Duration"
        tvDistance.text = "Distance"
        tvCalories.text = "Calories"
        tvHeartRate.text = "Heart Rate"
        tvComment.text = "Comment"

        tvDate.setOnClickListener {
            val cal = currentEntry.dateTime
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                currentEntry.dateTime = cal
                tvDate.text = "$day/${month+1}/$year"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        tvTime.setOnClickListener {
            val cal = currentEntry.dateTime
            TimePickerDialog(this, { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                currentEntry.dateTime = cal
                tvTime.text = String.format("%02d:%02d", hour, minute)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        tvDuration.setOnClickListener {
            showNumberInputDialog("Duration", "Enter duration in minutes") {
                val value = it.toDoubleOrNull() ?: 0.0
                currentEntry.duration = value * 60 // Convert minutes to seconds
                if (value > 0) {
                    tvDuration.text = "${value.toInt()} mins"
                } else {
                    tvDuration.text = "Duration"
                }
            }
        }

        tvDistance.setOnClickListener {
            showNumberInputDialog("Distance", "Enter distance in miles") {
                val value = it.toDoubleOrNull() ?: 0.0
                currentEntry.distance = value
                if (value > 0) {
                    tvDistance.text = "$value miles"
                } else {
                    tvDistance.text = "Distance"
                }
            }
        }

        tvCalories.setOnClickListener {
            showNumberInputDialog("Calories", "Enter calories") {
                val value = it.toDoubleOrNull() ?: 0.0
                currentEntry.calorie = value
                if (value > 0) {
                    tvCalories.text = "${value.toInt()} cals"
                } else {
                    tvCalories.text = "Calories"
                }
            }
        }

        tvHeartRate.setOnClickListener {
            showNumberInputDialog("Heart Rate", "Enter heart rate (bpm)") {
                val value = it.toDoubleOrNull() ?: 0.0
                currentEntry.heartRate = value
                if (value > 0) {
                    tvHeartRate.text = "${value.toInt()} bpm"
                } else {
                    tvHeartRate.text = "Heart Rate"
                }
            }
        }

        tvComment.setOnClickListener {
            showTextInputDialog("Comment", currentEntry.comment) {
                currentEntry.comment = it
                if (it.isNotEmpty()) {
                    tvComment.text = it
                } else {
                    tvComment.text = "Comment"
                }
            }
        }

        btnSave.setOnClickListener {
            saveExerciseEntry()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showNumberInputDialog(title: String, hint: String, onResult: (String) -> Unit) {
        val editText = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            this.hint = hint
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { _, _ -> onResult(editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTextInputDialog(title: String, initialValue: String, onResult: (String) -> Unit) {
        val editText = EditText(this).apply {
            setText(initialValue)
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { _, _ -> onResult(editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveExerciseEntry() {
        lifecycleScope.launch {
            try {
                repository.insert(currentEntry)
                Toast.makeText(
                    this@ManualActivity,
                    "Exercise saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ManualActivity,
                    "Error saving exercise: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}