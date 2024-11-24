package com.example.travelogue.table_journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.travelogue.table_country.CountryRepository
import com.example.travelogue.table_country.CountryViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

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

class JournalViewModelFactory (private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java))
            return JournalViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}