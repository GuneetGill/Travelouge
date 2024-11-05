package com.example.travelogue.ui.home

import android.content.Context
import android.icu.util.Calendar
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.travelogue.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

// Once database is up and running, replace JournalEntriesList will be a list of Journal Entry Objects
class JournalEntriesListAdapter(private val context: Context, private var JournalEntriesList: List<Map<String, String>>) : BaseAdapter(){

    // Change List<String> to JournalEntry when database is working
    override fun getItem(position: Int): Map<String,String> {
        return JournalEntriesList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return JournalEntriesList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.journal_entry_layout_adapter,null)

        // locate elements
        val titleTextView = view.findViewById<TextView>(R.id.Title)
        val dateTextView = view.findViewById<TextView>(R.id.Date)
        val locationTextView = view.findViewById<TextView>(R.id.Location)

        // Set TextView text to data from list
        val entry = JournalEntriesList[position]
        titleTextView.text = entry["title"]
        dateTextView.text = entry["date"]
        locationTextView.text = entry["location"]

        return view
    }

    // Change List<String> to JournalEntry when database is working
    fun replace(newExerciseEntryList: List<Map<String, String>>){
        JournalEntriesList = newExerciseEntryList
    }
}