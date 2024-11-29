package com.example.travelogue.ui.journal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travelogue.R
import com.example.travelogue.Util
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.table_journal.Journal
import com.example.travelogue.table_journal.JournalDao
import com.example.travelogue.table_journal.JournalRepository
import com.example.travelogue.table_journal.JournalViewModel
import com.example.travelogue.table_journal.JournalViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.ERROR_NETWORK
import android.speech.SpeechRecognizer.ERROR_NO_MATCH
import android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT
import android.text.Editable
import android.util.Log
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AddJournalFragment : Fragment(R.layout.fragment_add_journal) {

    private var photoUri: Uri? = null
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: JournalDao
    private lateinit var repository: JournalRepository
    private lateinit var viewModelFactory: JournalViewModelFactory
    private lateinit var journalViewModel: JournalViewModel

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val editTextThoughts = view.findViewById<EditText>(R.id.editTextThoughts)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val btnAddPhotos = view.findViewById<Button>(R.id.btnAddPhotos)
        val editJournalTitle = view.findViewById<EditText>(R.id.editJournalTitle)
        val speechTextButton = view.findViewById<ImageButton>(R.id.speech_text_button)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // Set desired language
        }
        val currentDate = getCurrentDate()

        // Initialize for DB interaction
        database = UserDatabase.getInstance(requireActivity())
        databaseDao = database.journalDao
        repository = JournalRepository(databaseDao)
        viewModelFactory = JournalViewModelFactory(repository)
        journalViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(JournalViewModel::class.java)

        // Get countryID
        val countryName = arguments?.getString("countryName")
        val countryID = arguments?.getLong("countryID")

        // Initialize activity result handlers
        initActivityResultHandlers()

        requestMicrophonePermission()

        btnAddPhotos.setOnClickListener {
            showImagePickerDialog()
        }

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val thoughts = editTextThoughts.text.toString()
            speechRecognizer.stopListening()
            if (thoughts.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please write down your thoughts!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Save the journal to the database
                val journal = Journal(
                    countryId = countryID!!,
                    title = editJournalTitle.text.toString(),
                    content = thoughts,
                    rating = rating,
                    date = currentDate,
                    photoUri = photoUri?.toString() // Convert URI to a String
                )

                // Insert into database using a coroutine
                journalViewModel.addJournal(journal)
                Toast.makeText(requireContext(), "Journal Added", Toast.LENGTH_SHORT).show()
                // Go back to country fragment
                val bundle = Bundle().apply {
                    putLong("countryID", countryID)
                    putString("countryName", countryName)
                }
                findNavController().navigate(R.id.countryFragment, bundle)
            }
        }

        //speech recognizer listener

        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("Speech", "Ready to listen...")
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
                speechRecognizer.stopListening()
            }

            override fun onError(error: Int) {

//                startListening()
            }

            override fun onResults(results: Bundle?) {
                // Process recognized speech results
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    val result = it[0]
                    editTextThoughts.append(Editable.Factory.getInstance().newEditable(result))
                    editTextThoughts.append(" ")
                }

                startListening()
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }

        speechRecognizer.setRecognitionListener(recognitionListener)


        //start listener
        speechTextButton.setOnClickListener {
            startListening()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun initActivityResultHandlers() {
        // Handle camera result
        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) { // Fixed reference to Activity.RESULT_OK
                    Toast.makeText(
                        requireContext(),
                        "Photo captured successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    photoUri = null
                    Toast.makeText(
                        requireContext(),
                        "Camera action was canceled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        // Handle gallery picker result
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    photoUri = uri
                    Toast.makeText(
                        requireContext(),
                        "Image selected successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    photoUri = null // Reset the URI if the user cancels
                    Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take a Photo", "Choose from Files")
        // check permissions first
        Util.checkPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Add Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> pickFromFiles()
                }
            }
            .show()
    }

    private fun openCamera() {
        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            "temp_photo_${System.currentTimeMillis()}.jpg"
        )
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.travelogue.fileprovider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        cameraResultLauncher.launch(intent)
    }

    private fun pickFromFiles() {
        galleryResultLauncher.launch("image/*")
    }

    private fun requestMicrophonePermission() {
        // Check for microphone permission and request if not granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    private fun startListening() {
        // Configure SpeechRecognizer Intent
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking...")
        }

        // Start listening for speech
        speechRecognizer.startListening(recognizerIntent)
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied
                Toast.makeText(
                    requireContext(),
                    "Microphone permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.stopListening()
        speechRecognizer.destroy() // Clean up the speech recognizer
    }

}
