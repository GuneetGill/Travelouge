package com.example.travelogue.ui.doc

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.travelogue.R
import com.example.travelogue.table_folder.Folder

class DocFolderListAdapter(private val context: Context, private var folderList: List<Folder>) : BaseAdapter(){

    override fun getItem(position: Int): Any {
        return folderList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return folderList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.doc_folders_adapter_layout,null)

        val textViewFolderName = view.findViewById(R.id.folder_name) as TextView
        textViewFolderName.text = folderList.get(position).folderName

        return view
    }

    fun replace(newFolderList: List<Folder>){
        folderList = newFolderList
    }

}