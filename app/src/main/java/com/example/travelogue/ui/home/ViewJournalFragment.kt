package com.example.travelogue.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.travelogue.R
import androidx.navigation.fragment.findNavController
import com.example.travelogue.Util


class ViewJournalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get all the journal data
        val journalTitle = arguments?.getString("title")
        val journalRating = arguments?.getFloat("rating")
        val journalContent = arguments?.getString("content")
        val journalPhotoUri = arguments?.getString("photoUri")


        // set title of toolbar to journal title
        (activity as? AppCompatActivity)?.supportActionBar?.title = journalTitle

        // set img
        val imageView = view.findViewById<ImageView>(R.id.journalImg)

        // if image was added set it, otherwise just use the default image
        if (!journalPhotoUri.isNullOrEmpty()) {
            Util.checkPermissions(requireActivity(), arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ))
            println("debug: journal image detected, setting image")
            imageView.setImageURI(journalPhotoUri!!.toUri())
        }
        else {
            imageView.setImageResource(R.drawable.default_journal_image)
        }

        // set star rating
        val rating = view.findViewById<RatingBar>(R.id.JournalRating)
        if (journalRating != null) {
            rating.rating = journalRating
        }
        else {
            rating.rating = 0f
        }

        // set description
        val descriptionTextView = view.findViewById<TextView>(R.id.desciption)
        descriptionTextView.text = journalContent

        // Set onclick for expenses button
        val expensesButton = view.findViewById<Button>(R.id.button)
        expensesButton.setOnClickListener {
            findNavController().navigate(R.id.ExpenseFragment)
        }
    }
}