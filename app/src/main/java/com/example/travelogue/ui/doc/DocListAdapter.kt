package com.example.travelogue.ui.doc

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.travelogue.R
import com.example.travelogue.doc_table.Document
import com.example.travelogue.folder_table.Folder

class DocListAdapter(private val context: Context, private var docList: List<Document>) : BaseAdapter(){

    override fun getItem(position: Int): Any {
        return docList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return docList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.doc_adapter_layout,null)

        val imgViewDoc = view.findViewById(R.id.doc_photo) as ImageView

        val byteArray = docList[position].docImgData
        if (byteArray.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            imgViewDoc.setImageBitmap(bitmap)
        } else {
            // Set a placeholder image if no image data is available
//            imgViewDoc.setImageResource(R.drawable.placeholder_image)
        }

        return view
    }

    fun replace(newDocList: List<Document>){
        docList = newDocList
    }

}