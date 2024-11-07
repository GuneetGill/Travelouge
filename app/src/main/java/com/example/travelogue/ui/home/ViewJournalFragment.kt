package com.example.travelogue.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.travelogue.R


class ViewJournalFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set title of toolbar to journal title
        val journalTitle = arguments?.getString("journalTitle")
        (activity as? AppCompatActivity)?.supportActionBar?.title = journalTitle

        // set img
        val imageView = view.findViewById<ImageView>(R.id.journalImg)
        imageView.setImageResource(R.drawable.nuuk_greenland_sample)

        // set star rating
        val rating = view.findViewById<RatingBar>(R.id.JournalRating)
        rating.rating = 3.0f

        // set description
        val descriptionTextView = view.findViewById<TextView>(R.id.desciption)
        descriptionTextView.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

        // set onclick for expenses button
        // ...
    }
}