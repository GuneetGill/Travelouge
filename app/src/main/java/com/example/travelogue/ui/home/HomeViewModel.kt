// HomeViewModel.kt
package com.example.travelogue.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class JournalEntry(
    val title: String,
    val date: String,
    val location: String,
    val rating: Float,
    val thoughts: String
)

data class Country(
    val name: String,
    val journals: List<JournalEntry>
)

class HomeViewModel : ViewModel() {

    private val fakeData = listOf(
        Country(
            name = "Greenland",
            journals = listOf(
                JournalEntry(
                    title = "Trip to Greenland",
                    date = "2024-11-10",
                    location = "Nuuk, Greenland",
                    rating = 4.5f,
                    thoughts = "A beautiful icy landscape with friendly locals."
                )
            )
        ),
        Country(
            name = "Brazil",
            journals = listOf(
                JournalEntry(
                    title = "Carnival Experience",
                    date = "2024-02-25",
                    location = "Rio de Janeiro, Brazil",
                    rating = 5.0f,
                    thoughts = "The energy and colors were unforgettable!"
                )
            )
        )
    )

    private val _countriesLiveData = MutableLiveData<List<Country>>(fakeData)
    val countriesLiveData: LiveData<List<Country>> get() = _countriesLiveData
}
