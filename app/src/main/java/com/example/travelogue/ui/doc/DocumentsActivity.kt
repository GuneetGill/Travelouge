package com.example.travelogue.ui.doc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.Dialogs
import com.example.travelogue.R
import com.example.travelogue.db.Folder
import com.example.travelogue.db.TravelogueDatabase
import com.example.travelogue.db.FolderDao
import com.example.travelogue.db.FolderRepository
import com.example.travelogue.db.TravelogueViewModel
import com.example.travelogue.db.TravelogueViewModelFactory

class DocumentsActivity : AppCompatActivity() {
    private lateinit var addFolderButton : Button
    private lateinit var database: TravelogueDatabase
    private lateinit var databaseDao: FolderDao
    private lateinit var repository: FolderRepository
    private lateinit var viewModelFactory: TravelogueViewModelFactory
    private lateinit var travelogueViewModel: TravelogueViewModel
    private lateinit var arrayList: ArrayList<Folder>
    private lateinit var arrayAdapter: DocFolderListAdapter
    private lateinit var gridView: GridView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        gridView = findViewById(R.id.folder_gv)
        //db initialize
        database = TravelogueDatabase.getInstance(this)
        databaseDao = database.folderDao
        repository = FolderRepository(databaseDao)
        viewModelFactory = TravelogueViewModelFactory(repository)
        travelogueViewModel =
            ViewModelProvider(this, viewModelFactory).get(TravelogueViewModel::class.java)

        arrayList = ArrayList()
        arrayAdapter = DocFolderListAdapter(this, arrayList)
        gridView.adapter = arrayAdapter

        travelogueViewModel.allTravelsLiveData.observe(this, Observer { travel ->
            arrayAdapter.replace(travel)
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
            startActivity(intent)
        }

    }
}
