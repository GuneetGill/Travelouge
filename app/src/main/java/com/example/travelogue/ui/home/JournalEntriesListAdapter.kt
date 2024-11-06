// JournalEntriesListAdapter.kt
package com.example.travelogue.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.travelogue.R

class JournalEntriesListAdapter(
    private val context: Context,
    private var journalEntriesList: List<JournalEntry>
) : BaseAdapter() {

    override fun getCount(): Int {
        return journalEntriesList.size
    }

    override fun getItem(position: Int): JournalEntry {
        return journalEntriesList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.journal_entry_layout_adapter, parent, false)

        val titleTextView = view.findViewById<TextView>(R.id.Title)
        val dateTextView = view.findViewById<TextView>(R.id.Date)
        val locationTextView = view.findViewById<TextView>(R.id.Location)

        val entry = journalEntriesList[position]
        titleTextView.text = entry.title
        dateTextView.text = entry.date
        locationTextView.text = entry.location

        return view
    }

    fun replace(newJournalEntryList: List<JournalEntry>) {
        journalEntriesList = newJournalEntryList
        notifyDataSetChanged()
    }
}
