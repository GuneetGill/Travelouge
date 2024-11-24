package com.example.travelogue.ui.doc

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.AlertDialog
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.R
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.doc_table.Document
import com.example.travelogue.doc_table.DocumentDao
import com.example.travelogue.doc_table.DocumentRepository
import com.example.travelogue.doc_table.DocumentViewModel
import com.example.travelogue.doc_table.DocumentViewModelFactory
import com.example.travelogue.folder_table.Folder
import com.example.travelogue.folder_table.FolderRepository
import com.example.travelogue.folder_table.FolderViewModel
import com.example.travelogue.folder_table.FolderViewModelFactory
import java.io.File

class FolderActivity : AppCompatActivity() {
    private lateinit var returnButton: Button
    private lateinit var deleteFButton: Button
    private lateinit var addButton: Button
    private lateinit var imageView: ImageView
    private lateinit var gridView: GridView
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>
    private lateinit var tempImageUri: Uri

    private lateinit var database: UserDatabase  // Ensure the right database type is being used
    private lateinit var databaseDao: DocumentDao
    private lateinit var repository: DocumentRepository
    private lateinit var viewModelFactory: DocumentViewModelFactory
    private lateinit var arrayAdapter: DocListAdapter
    private lateinit var viewModel: DocumentViewModel
    private lateinit var arrayList: ArrayList<Document>
    private var ownerFolderId: Long = -1L
    private val document = Document()
//    private lateinit var arrayList: ArrayList<Folder>
//    private lateinit var arrayAdapter: DocFolderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        returnButton = findViewById(R.id.return_button)
        deleteFButton = findViewById(R.id.delete_folder_button)
        addButton = findViewById(R.id.add_button)
//        imageView = findViewById(R.id.dynamic_image_view)
        gridView = findViewById(R.id.doc_gv)

        database = UserDatabase.getInstance(this)
        databaseDao = database.documentDao
        repository = DocumentRepository(databaseDao)
        viewModelFactory = DocumentViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DocumentViewModel::class.java)
        arrayList = ArrayList()
        arrayAdapter = DocListAdapter(this, arrayList)
        gridView.adapter = arrayAdapter

        returnButton.setOnClickListener { finish() }
        addButton.setOnClickListener { showImagePickerDialog() }
        ownerFolderId = intent.getLongExtra("folderId", -1)
        viewModel.allDocumentsLiveData.observe(this, Observer { docs ->
//            arrayAdapter.replace(filteredFolders)
//            arrayAdapter.notifyDataSetChanged()
            Log.d("owner id",ownerFolderId.toString())
            val filteredDocs = docs.filter { it.folderOwnerId == ownerFolderId }
            arrayAdapter.replace(filteredDocs)
            arrayAdapter.notifyDataSetChanged()
        })

        // Initialize activity result handlers
        initActivityResultHandlers()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take a Photo", "Choose from Files")
        AlertDialog.Builder(this)
            .setTitle("Add Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpenCamera()
                    1 -> checkStoragePermissionAndPickFromFiles()
                }
            }
            .show()
    }

    private fun initActivityResultHandlers() {
        // Handle camera result
        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Display the captured image
//                    imageView.setImageURI(tempImageUri)
                    tempImageUri?.let { uri ->
                        saveImageToDatabase(uri)
                    }

                    Toast.makeText(this, "Photo captured successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Camera action was canceled", Toast.LENGTH_SHORT).show()
                }
            }

        // Handle gallery picker result
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
//                    imageView.setImageURI(it)
                    Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun openCamera() {
        // Create a temporary file for the captured image
        val photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "temp_photo_${System.currentTimeMillis()}.jpg"
        )
        tempImageUri =
            FileProvider.getUriForFile(this, "com.example.travelogue.fileprovider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
        }

        cameraResultLauncher.launch(intent)
    }

    private fun pickFromFiles() {
        galleryResultLauncher.launch("image/*")
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkStoragePermissionAndPickFromFiles() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            pickFromFiles()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    pickFromFiles()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //converter
    private fun uriToByteArray(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }

    //save captured photo to the database
    private fun saveImageToDatabase(uri: Uri) {
        val imageData = uriToByteArray(uri)
        document.docImgData = imageData
        document.folderOwnerId = intent.getLongExtra("folderId", -1)
        viewModel.insertDocument(document)
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }
}
