package com.example.travelogue.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel(/* private val repository: TravelogueRepository */) {

    // When DB is working, add all the DB operations like insert, delete to the view model as methods
    private val fakeData = arrayListOf(
        mapOf(
            "title" to "Trip to Greenland",
            "date" to "2024-11-10",
            "location" to "Nuuk, Greenland"
        ),
        mapOf(
            "title" to "Trip to New York",
            "date" to "2024-11-15",
            "location" to "New York, USA"
        ),
        mapOf(
            "title" to "Trip to Italy",
            "date" to "2024-11-20",
            "location" to "Rome, Italy"
        )
    )

    val allJournalEntriesLiveData: LiveData<List<Map<String, String>>> = MutableLiveData(fakeData) // repository.allJournalEntries.asLiveData()
}