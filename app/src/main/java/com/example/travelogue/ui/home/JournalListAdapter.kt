package com.example.travelogue.ui.home

import android.content.Context
import android.icu.util.Calendar
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.travelogue.table_journal.Journal
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class JournalListAdapter(private val context: Context, private var journalList: List<Journal>) : BaseAdapter(){

    override fun getItem(position: Int): Journal {
        return journalList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return journalList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, android.R.layout.simple_list_item_1,null)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = journalList[position].title
        return view
    }

    fun replace(newJournalList: List<Journal>){
        journalList = newJournalList
    }
}