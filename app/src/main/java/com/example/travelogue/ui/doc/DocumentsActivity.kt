package com.example.travelogue.ui.doc

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.travelogue.folder_table.Folder
import com.example.travelogue.folder_table.FolderDao
import com.example.travelogue.folder_table.FolderRepository
import com.example.travelogue.folder_table.FolderViewModel
import com.example.travelogue.folder_table.FolderViewModelFactory


class DocumentsActivity : AppCompatActivity() {
    private lateinit var addFolderButton: Button
    private lateinit var database: UserDatabase  // Ensure the right database type is being used
    private lateinit var databaseDao: FolderDao
    private lateinit var repository: FolderRepository
    private lateinit var viewModelFactory: FolderViewModelFactory
    private lateinit var viewModel: FolderViewModel
    private lateinit var arrayList: ArrayList<Folder>
    private lateinit var arrayAdapter: DocFolderListAdapter
    private lateinit var gridView: GridView
    private lateinit var returnButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        returnButton = findViewById(R.id.return_button)

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
            intent.putExtra("folderId", clickedFolder.folder_id)
            startActivity(intent)
        }

        returnButton.setOnClickListener {
            finish()
        }

    }
}
