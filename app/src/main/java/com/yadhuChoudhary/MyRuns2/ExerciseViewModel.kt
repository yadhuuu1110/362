package com.yadhuChoudhary.MyRuns2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository
    val allExercises: LiveData<List<ExerciseEntry>>

    init {
        val database = ExerciseDatabase.getDatabase(application)
        repository = ExerciseRepository(database.exerciseDao())
        allExercises = repository.allExercises
    }
}