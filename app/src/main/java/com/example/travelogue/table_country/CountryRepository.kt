package com.example.travelogue.table_country

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CountryRepository(private val countryDao: CountryDao) {
    val allCountries: Flow<List<Country>> = countryDao.getAllCountries()

    fun insertCountry(country: Country) {
        CoroutineScope(IO).launch {
            countryDao.insertCountry(country)
        }
    }

    fun deleteCountry(country: Country) {
        CoroutineScope(IO).launch {
            countryDao.deleteCountry(country)
        }
    }
}