package com.example.travelogue

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.db.Folder
import com.example.travelogue.db.TravelogueDatabase
import com.example.travelogue.db.FolderDao
import com.example.travelogue.db.FolderRepository
import com.example.travelogue.db.TravelogueViewModel
import com.example.travelogue.db.TravelogueViewModelFactory

class Dialogs : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        const val DOC_KEY = "doc"
        const val DOC_VAL = 1
    }

    private lateinit var database: TravelogueDatabase
    private lateinit var databaseDao: FolderDao
    private lateinit var repository: FolderRepository
    private lateinit var viewModelFactory: TravelogueViewModelFactory
    private lateinit var travelogueViewModel: TravelogueViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DOC_KEY)

        //db initialize
        database = TravelogueDatabase.getInstance(requireContext())
        databaseDao = database.folderDao
        repository = FolderRepository(databaseDao)
        viewModelFactory = TravelogueViewModelFactory(repository)
        travelogueViewModel =
            ViewModelProvider(this, viewModelFactory).get(TravelogueViewModel::class.java)
        //travel object
        val folder = Folder()

        if (dialogId == DOC_VAL) {
            val builder = AlertDialog.Builder(requireActivity())
            val view: View = requireActivity().layoutInflater.inflate(
                R.layout.fragment_doc_dialog,
                null
            )
            builder.setView(view)
            builder.setTitle("Enter Folder Name")
            //get the text from the dialog
            val getFolderName: EditText = view.findViewById(R.id.folder_name_et)
            builder.setPositiveButton("OK") { dialog, _ ->
                val folderName = getFolderName.text.toString()
                folder.folderName = folderName
                travelogueViewModel.insertFolder(folder)
//                Toast.makeText(activity, folderName, Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("cancel", this)
            ret = builder.create()
        }
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}