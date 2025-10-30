package com.yadhuChoudhary.MyRuns2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.util.*

//Ideas similar to ActiontabsKotlin
class FragmentB : Fragment() {

    private lateinit var listView: ListView
    private lateinit var repository: ExerciseRepository
    private var exercises = mutableListOf<ExerciseEntry>()
    private lateinit var adapter: ExerciseEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_b, container, false)

        listView = view.findViewById(R.id.list_view_history)

        // Initialize database and repository
        val database = ExerciseDatabase.getDatabase(requireContext())
        repository = ExerciseRepository(database.exerciseDao())

        // Setup adapter
        adapter = ExerciseEntryAdapter(requireContext(), exercises)
        listView.adapter = adapter

        // Observe database changes
        repository.allExercises.observe(viewLifecycleOwner, Observer { exerciseList ->
            exerciseList?.let {
                exercises.clear()
                exercises.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })

        // Setup item click listener
        listView.setOnItemClickListener { _, _, position, _ ->
            val exercise = exercises[position]

            val intent = if (exercise.inputType == Constants.INPUT_TYPE_MANUAL) {
                Intent(requireContext(), DisplayEntryActivity::class.java)
            } else {
                Intent(requireContext(), MapActivity::class.java)
            }

            intent.putExtra(Constants.EXTRA_EXERCISE_ID, exercise.id)
            startActivity(intent)
        }

        return view
    }
}

// Adapter for exercise entries
class ExerciseEntryAdapter(
    context: android.content.Context,
    private val exercises: List<ExerciseEntry>
) : ArrayAdapter<ExerciseEntry>(context, R.layout.list_item_exercise, exercises) {

    private val dateFormat = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())

    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_exercise, parent, false)

        val exercise = exercises[position]

        val tvFirstRow = view.findViewById<TextView>(R.id.tv_first_row)
        val tvSecondRow = view.findViewById<TextView>(R.id.tv_second_row)

        // First row: Input Type: Activity Type, Date
        val inputType = when (exercise.inputType) {
            Constants.INPUT_TYPE_MANUAL -> "Manual Entry"
            Constants.INPUT_TYPE_GPS -> "GPS"
            Constants.INPUT_TYPE_AUTOMATIC -> "Automatic"
            else -> "Unknown"
        }

        val activityType = if (exercise.activityType < Constants.ACTIVITY_TYPES.size) {
            Constants.ACTIVITY_TYPES[exercise.activityType]
        } else {
            "Unknown"
        }

        val dateStr = dateFormat.format(exercise.dateTime.time)
        tvFirstRow.text = "$inputType: $activityType, $dateStr"

        // Second row: Distance, Duration
        val distanceStr = UnitConverter.formatDistance(exercise.distance, context)
        val durationStr = UnitConverter.formatDuration(exercise.duration)
        tvSecondRow.text = "$distanceStr, $durationStr"

        return view
    }
}