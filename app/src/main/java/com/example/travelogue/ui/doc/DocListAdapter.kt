package com.example.travelogue.ui.doc

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
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
        val txtViewDoc = view.findViewById(R.id.doc_name) as TextView
        val imgViewDoc = view.findViewById(R.id.doc_photo) as ImageView
        txtViewDoc.text = docList[position].docName
        val byteArray = docList[position].docImgData
        if (byteArray.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            imgViewDoc.setImageBitmap(bitmap)
        } else {

        }
        //to enlarge the photo when clicked
        imgViewDoc.setOnClickListener {
            showEnlargedImage(byteArray)
        }

        return view
    }

    fun replace(newDocList: List<Document>){
        docList = newDocList
    }

    private fun showEnlargedImage(imageData: ByteArray) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.enlarged_image_dialog, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.enlarged_image)

        // Decode the byte array and set the bitmap to the ImageView
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        imageView.setImageBitmap(bitmap)

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }


}