package com.yadhuChoudhary.MyRuns3

object Constants {
    // Input Types
    const val INPUT_TYPE_MANUAL = 0
    const val INPUT_TYPE_GPS = 1
    const val INPUT_TYPE_AUTOMATIC = 2

    // Activity Types
    val ACTIVITY_TYPES = arrayOf(
        "Running",
        "Walking",
        "Standing",
        "Cycling",
        "Hiking",
        "Downhill Skiing",
        "Cross-Country Skiing",
        "Snowboarding",
        "Skating",
        "Swimming",
        "Mountain Biking",
        "Wheelchair",
        "Elliptical",
        "Other"
    )

    // Dialog IDs
    const val DIALOG_DATE = 1
    const val DIALOG_TIME = 2
    const val DIALOG_DURATION = 3
    const val DIALOG_DISTANCE = 4
    const val DIALOG_CALORIES = 5
    const val DIALOG_HEART_RATE = 6
    const val DIALOG_COMMENT = 7

    // Intent Keys
    const val EXTRA_ACTIVITY_TYPE = "activity_type"
    const val EXTRA_INPUT_TYPE = "input_type"
    const val EXTRA_EXERCISE_ID = "exercise_id"

    // SharedPreferences Keys
    const val PREFS_NAME = "MyRunsPrefs"
    const val PREF_UNIT_PREFERENCE = "unit_preference"

    // Unit Preferences
    const val UNIT_METRIC = "Kilometers"
    const val UNIT_IMPERIAL = "Miles"

    // Conversion Factors
    const val MILES_TO_KM = 1.60934
    const val FEET_TO_METERS = 0.3048
}