package com.example.travelogue.table_journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    fun addJournal(journal: Journal) = viewModelScope.launch {
        repository.addJournal(journal)
    }

    fun getJournalsByCountry(countryId: Long): LiveData<List<Journal>> {
        val journals = MutableLiveData<List<Journal>>()
        viewModelScope.launch {
            journals.postValue(repository.getJournalsByCountry(countryId))
        }
        return journals
    }

    fun deleteJournal(journal: Journal) = viewModelScope.launch {
        repository.deleteJournal(journal)
    }
}