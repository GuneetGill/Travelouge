package com.example.travelogue.ui.doc

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.Dialogs
import com.example.travelogue.Globals.PREF_NAME
import com.example.travelogue.R
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.table_folder.Folder
import com.example.travelogue.table_folder.FolderDao
import com.example.travelogue.table_folder.FolderRepository
import com.example.travelogue.table_folder.FolderViewModel
import com.example.travelogue.table_folder.FolderViewModelFactory


class DocumentsActivity : AppCompatActivity() {
    private lateinit var addFolderButton: Button
    private lateinit var returnButton: Button  // Define returnButton as needed
    private lateinit var database: UserDatabase  // Ensure the right database type is being used
    private lateinit var databaseDao: FolderDao
    private lateinit var repository: FolderRepository
    private lateinit var viewModelFactory: FolderViewModelFactory
    private lateinit var viewModel: FolderViewModel
    private lateinit var arrayList: ArrayList<Folder>
    private lateinit var arrayAdapter: DocFolderListAdapter
    private lateinit var gridView: GridView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        //userid
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)

        gridView = findViewById(R.id.folder_gv)
        //db initialize
        database = UserDatabase.getInstance(this)
        databaseDao = database.folderDao
        repository = FolderRepository(databaseDao)
        viewModelFactory = FolderViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(FolderViewModel::class.java)

        arrayList = ArrayList()
        arrayAdapter = DocFolderListAdapter(this, arrayList)
        gridView.adapter = arrayAdapter

        viewModel.allFoldersLiveData.observe(this, Observer { folders ->
            val filteredFolders = folders.filter { it.userOwnerId == userId }
            arrayAdapter.replace(filteredFolders)
            arrayAdapter.notifyDataSetChanged()
        })

//        viewModel.getFolderByUser(userId).observe(this, Observer { folder ->
//            arrayAdapter.replace(folder)
//            arrayAdapter.notifyDataSetChanged()
//        })

        addFolderButton = findViewById(R.id.add_folder_button)

        addFolderButton.setOnClickListener {
            val myDialog = Dialogs()
            val bundle = Bundle()
            bundle.putInt(Dialogs.DOC_KEY, Dialogs.DOC_VAL)
            myDialog.arguments = bundle
            myDialog.show(supportFragmentManager, "my dialog")
        }

        // Set onItemClickListener for the GridView
        gridView.setOnItemClickListener { parent, view, position, id ->
            val clickedFolder = parent.getItemAtPosition(position) as Folder
            // Show a toast with the folder_id of the clicked item
            Toast.makeText(this, "Folder ID: ${clickedFolder.folder_id}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FolderActivity::class.java)
            startActivity(intent)
        }


//        viewModel.allFoldersLiveData.observe(this, Observer { folders ->
//            if (folders.isNotEmpty()) {
//                // Log each folder individually
//                folders.forEach { folder ->
//                    Log.d("DocumentsActivitye", "Folder ID: ${folder.folder_id}, Folder Name: ${folder.folderName}, User Owner ID: ${folder.userOwnerId}, Created At: ${folder.createdAt}")
//                }
//            } else {
//                Log.d("DocumentsActivity", "No folders found in the database.")
//            }
//
//            arrayAdapter.replace(folders)
//            arrayAdapter.notifyDataSetChanged()
//        })

    }
}
