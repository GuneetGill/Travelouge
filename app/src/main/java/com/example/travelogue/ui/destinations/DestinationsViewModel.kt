package com.example.travelogue.ui.destinations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DestinationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Destinations Fragment"
    }
    val text: LiveData<String> = _text
}