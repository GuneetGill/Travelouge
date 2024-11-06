// AddJournalFragment.kt
package com.example.travelogue.ui.journal

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travelogue.R

class AddJournalFragment : Fragment(R.layout.fragment_add_journal) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val editTextThoughts = view.findViewById<EditText>(R.id.editTextThoughts)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val thoughts = editTextThoughts.text.toString()
            Toast.makeText(requireContext(), "Journal saved!", Toast.LENGTH_SHORT).show()
            // Here you could save the journal entry to a database or shared preferences
        }
    }
}
