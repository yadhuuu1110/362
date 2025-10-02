package com.yadhuChoudhary.myruns1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class ProfileActivity : AppCompatActivity() {

    // Views — IDs match your activity_profile.xml exactly
    private lateinit var Name: EditText          // @id/Your_Name
    private lateinit var Email: EditText         // @id/Your_Email
    private lateinit var Ph: EditText            // @id/Your_Ph
    private lateinit var Eg: EditText            // @id/Eg
    private lateinit var Major: EditText         // @id/Your_Major
    private lateinit var Rgroup: RadioGroup      // @id/Radio_Group
    private lateinit var Female: RadioButton     // @id/Female
    private lateinit var Male: RadioButton       // @id/Male
    private lateinit var buttonSave: Button      // @id/Save
    private lateinit var buttonCancel: Button    // @id/Cancel
    private lateinit var buttonChange: Button    // @id/Change
    private lateinit var imageView: ImageView    // @id/imageView

    // Storage
    private lateinit var sharedPref: SharedPreferences
    private lateinit var picturesDir: File
    private lateinit var imageFile: File
    private lateinit var tempImageFile: File
    private lateinit var imageFileURI: Uri
    private lateinit var tempImageFileURI: Uri

    // Activity result launchers
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var requestCameraPermission: ActivityResultLauncher<String>

    // Pref keys
    private val PREFS = "MyRuns1Prefs"
    private val KEY_NAME = "name"
    private val KEY_EMAIL = "email"
    private val KEY_PHONE = "phone"
    private val KEY_CLASS = "class"
    private val KEY_MAJOR = "major"
    private val KEY_GENDER_ID = "gender_id"
    private val KEY_PHOTO_URI = "profile_image"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1) Find views by ID (matches your XML)
        buttonSave = findViewById(R.id.Save)
        buttonCancel = findViewById(R.id.Cancel)
        buttonChange = findViewById(R.id.Change)
        Name = findViewById(R.id.Your_Name)
        Email = findViewById(R.id.Your_Email)
        Ph = findViewById(R.id.Your_Ph)
        Eg = findViewById(R.id.Eg)
        Major = findViewById(R.id.Your_Major)
        Rgroup = findViewById(R.id.Radio_Group)
        Female = findViewById(R.id.Female)
        Male = findViewById(R.id.Male)
        imageView = findViewById(R.id.imageView)

        // 2) Prefs
        sharedPref = getSharedPreferences(PREFS, MODE_PRIVATE)

        // 3) Files & URIs (authority must match manifest)
        setupFilesAndUris()

        // 4) Launchers
        setupActivityResultLaunchers()

        // 5) Load previous values
        loadSavedData()

        // 6) Clicks
        buttonChange.setOnClickListener { launchCameraOrRequestPermission() }
        buttonSave.setOnClickListener { saveAll() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun setupFilesAndUris() {
        picturesDir = File(getExternalFilesDir(null), "Pictures").apply { mkdirs() }
        imageFile = File(picturesDir, "myProfilePhoto.jpg")
        tempImageFile = File(picturesDir, "tempProfilePhoto.jpg")

        // IMPORTANT: authority must equal <manifest package> + ".fileprovider"
        val authority = "com.yadhuChoudhary.myruns1.fileprovider"

        imageFileURI = FileProvider.getUriForFile(this, authority, imageFile)
        tempImageFileURI = FileProvider.getUriForFile(this, authority, tempImageFile)
    }

    private fun setupActivityResultLaunchers() {
        cameraResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Show the temp photo immediately
                val bmp = BitmapFactory.decodeFile(tempImageFile.absolutePath)
                imageView.setImageBitmap(bmp)
            }
        }

        requestCameraPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchCameraOrRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        // Some camera apps require the file to exist beforehand
        tempImageFile.parentFile?.mkdirs()
        if (!tempImageFile.exists()) tempImageFile.createNewFile()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileURI)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = android.content.ClipData.newRawUri("output", tempImageFileURI)
        }
        cameraResult.launch(intent)
    }

    private fun loadSavedData() {
        Name.setText(sharedPref.getString(KEY_NAME, ""))
        Email.setText(sharedPref.getString(KEY_EMAIL, ""))
        Ph.setText(sharedPref.getString(KEY_PHONE, ""))
        Eg.setText(sharedPref.getString(KEY_CLASS, ""))
        Major.setText(sharedPref.getString(KEY_MAJOR, ""))

        val savedGenderId = sharedPref.getInt(KEY_GENDER_ID, -1)
        if (savedGenderId != -1) Rgroup.check(savedGenderId) else Rgroup.clearCheck()

        sharedPref.getString(KEY_PHOTO_URI, null)?.let { saved ->
            runCatching { Uri.parse(saved) }.getOrNull()?.let { imageView.setImageURI(it) }
        }
    }

    private fun saveAll() {
        val editor = sharedPref.edit()
        editor.putString(KEY_NAME, Name.text.toString())
        editor.putString(KEY_EMAIL, Email.text.toString())
        editor.putString(KEY_PHONE, Ph.text.toString())
        editor.putString(KEY_CLASS, Eg.text.toString())
        editor.putString(KEY_MAJOR, Major.text.toString())
        editor.putInt(KEY_GENDER_ID, Rgroup.checkedRadioButtonId)

        // If we have a fresh temp photo, copy to permanent and store permanent URI
        if (tempImageFile.exists() && tempImageFile.length() > 0L) {
            tempImageFile.copyTo(imageFile, overwrite = true)
            editor.putString(KEY_PHOTO_URI, imageFileURI.toString())
        }

        editor.apply()
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}