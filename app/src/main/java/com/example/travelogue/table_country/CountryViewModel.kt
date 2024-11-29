package com.example.travelogue.table_country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelogue.table_journal.Journal
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CountryViewModel(private val repository: CountryRepository): ViewModel() {
    //val allCountriesLiveData: LiveData<List<Country>> = repository.allCountries.asLiveData()

    fun insertCountry(country: Country) {
        repository.insertCountry(country)
    }

    fun getCountriesByUserId(userId: Long): LiveData<List<Country>> {
        val journals = MutableLiveData<List<Country>>()
        viewModelScope.launch {
            journals.postValue(repository.getCountriesByUserId(userId))
        }
        return journals
    }

    fun deleteCountry(country: Country) {
        repository.deleteCountry(country)
    }
}

class CountryViewModelFactory (private val repository: CountryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(CountryViewModel::class.java))
            return CountryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}