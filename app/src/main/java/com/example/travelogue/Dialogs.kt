package com.example.travelogue

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelogue.Globals.PREF_NAME
import com.example.travelogue.db_user.UserDatabase
import com.example.travelogue.folder_table.Folder
import com.example.travelogue.folder_table.FolderDao
import com.example.travelogue.folder_table.FolderRepository
import com.example.travelogue.folder_table.FolderViewModel
import com.example.travelogue.folder_table.FolderViewModelFactory


class Dialogs : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        const val DOC_KEY = "doc"
        const val DOC_VAL = 1
    }

    private lateinit var database: UserDatabase
    private lateinit var databaseDao: FolderDao
    private lateinit var repository: FolderRepository
    private lateinit var viewModelFactory: FolderViewModelFactory
    private lateinit var viewModel: FolderViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DOC_KEY)
        //get userId

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)
//        //db initialize
        database = UserDatabase.getInstance(requireContext())
        databaseDao = database.folderDao
        repository = FolderRepository(databaseDao)
        viewModelFactory = FolderViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(FolderViewModel::class.java)
//        //travel object
        val folder = Folder()

        if (dialogId == DOC_VAL) {
            Log.d("myuserid", "User : $userId")
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
                folder.userOwnerId = userId
                viewModel.insertFolder(folder)
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